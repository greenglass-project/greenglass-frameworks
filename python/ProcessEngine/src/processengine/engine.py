#!/usr/bin/env python

import asyncio
import importlib.util
import logging
import sys

from websockets import serve

from . import configuration
from . import variables

logger = logging.getLogger(__name__)


class Engine:
    """The Process Engine"""
    background_tasks = set()

    def __init__(self):
        self.configuration = configuration.Configuration()
        self.variables = variables.Variables(self.configuration.processes)
        self.processes = []
        for process in self.configuration.processes:
            self.processes.append(process["processId"])

    async def server(self):
        """Task to run the web-socket server using the configured hostname and port"""
        await self.variables.start()

        host = self.configuration.engine_host
        port = self.configuration.engine_port
        print("Starting Websocket server on " + host + ":" + str(port))
        async with serve(self.variables.handler, host, port):
            await asyncio.Future()  # run forever

    def find_process(self, p):
        try:
            file = self.configuration.config_root + "/processes/" + p + ".py"
            print("Loading process " + p + " from " + file)

            spec = importlib.util.spec_from_file_location(p, file)
            module = importlib.util.module_from_spec(spec)
            sys.modules[p] = module
            spec.loader.exec_module(module)
            clazz = getattr(module, p)
            return clazz()
        except Exception as e:
            print(e)
            return None

    async def run(self):
        """Run the engine as a set of tasks"""
        engine = Engine()
        engine.background_tasks.add(asyncio.create_task(engine.server()))

        for process in self.processes:
            instance = self.find_process(process)
            if instance is not None:
                engine.background_tasks.add(asyncio.create_task(instance.run()))

        await asyncio.gather(*asyncio.all_tasks() - {asyncio.current_task()})


if __name__ == '__main__':
    engine = Engine()
    loop = asyncio.new_event_loop()
    asyncio.run(engine.run())
