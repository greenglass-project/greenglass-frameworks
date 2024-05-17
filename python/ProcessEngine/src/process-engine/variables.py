import logging
import asyncio
import functools
from sync_variable_value import SyncVariableValue
from variable_value_message import VariableValueMessage
from asyncio import get_running_loop

import rx
from rx.scheduler.eventloop import AsyncIOScheduler
from rx.disposable import Disposable

from websockets.server import serve
from rx import create
import json

logger = logging.getLogger(__name__)


class VariableNotFoundError(Exception):
    """Raised when a variable doesn't exist"""
    pass


def create_observable(sync_variable, loop):
    def on_subscribe(observer, scheduler):
        async def _aio_sub():
            try:
                while True:
                    v = await sync_variable.get_value()
                    observer.on_next(v)
                    loop.call_soon(observer.on_completed)
            except Exception as e:
                loop.call_soon(
                    functools.partial(observer.on_error, e))

        task = asyncio.ensure_future(_aio_sub(), loop=loop)
        return Disposable(task.cancel())

    return rx.create(on_subscribe)


class ObservableCtx:
    def __init__(self, sync_variable, observable):
        self.sync_variable = sync_variable
        self.observable = observable


class Variables:
    """The Variables"""

    def __init__(self, processes):
        self.processes = processes
        self.input_variables = {}
        self.output_variables = {}
        self.websocket = None

        for process in self.processes:
            process_id = process["processId"]
            input_variable_map = {}
            output_variable_map = {}

            self.input_variables[process_id] = input_variable_map
            self.output_variables[process_id] = output_variable_map

            # Create a map of process_id/variable_id to observable
            # for input variables
            for variable in process["inputVariables"]:
                sync_value = SyncVariableValue()
                input_variable_map[variable["variableId"]] = ObservableCtx(
                    sync_value, # setting this triggers the observable
                    create_observable(sync_value, get_running_loop()) # the observable
                )

            # Create a map of process_id/variable_id to type
            # for output variables
            for variable in process["outputVariables"]:
                output_variable_map[variable["variableId"]] = variable["type"]

    async def handler(self, websocket):
        self.websocket = websocket
        async for message in websocket:
            print("Received message:", message)
            var_message = json.loads(message)
            process_id = var_message["processId"]
            variable_id = var_message["variableId"]
            value = var_message["value"]["value"]

            # lookup the variable by process_id and variable_id
            # if it exists set the synchronised variable - which
            # will trigger the observable
            ctx = self.input_variables[process_id][variable_id]
            if ctx is not None:
                await ctx.sync_variable.set_value(value)

            print("PROCESS-ID " + process_id + " VARIABLE-ID " + variable_id)

    def subscribe_to_variable(self, process_id, variable_id):
        """Subscribe to a variable by process_id and variable_id. Returns the observable"""
        ctx = self.input_variables[process_id][variable_id]
        if ctx is None:
            raise VariableNotFoundError("Variable for " + process_id + " " + variable_id + " not found")
        return ctx.observable

    def publish_to_variable(self, process_id, variable_id, value) :
        """Publish to variable by process_id and variable_id via the web-socket connection """
        type = self.output_variables[process_id][variable_id]
        if type is None:
            raise VariableNotFoundError("Variable for " + process_id + " " + variable_id + " not found")

        # Create a V
        message = VariableValueMessage(process_id, variable_id, type, value)

        # if the websocket is active send the message
        if self.websocket is not None:
            self.websocket.send(message.toJSON())