#!/usr/bin/env python

import asyncio
import logging

from websockets import serve

from configuration import Configuration
from variables import Variables

logger = logging.getLogger(__name__)


class Engine:
    """The Process Engine"""
    background_tasks = set()

    def __init__(self):
        self.configuration = Configuration()
        self.variables = Variables(self.configuration.processes)

    async def server(self):
        """Task to run the web-socket server using the configured hostname and port"""
        host = self.configuration.engine_host
        port = self.configuration.engine_port
        print("Starting Websocket server on " + host + ":" + str(port))
        async with serve(self.variables.handler, host, port):
            await asyncio.Future()  # run forever


async def main():
    """Run the engine as a set of tasks"""
    engine = Engine()
    engine.background_tasks.add(asyncio.create_task(engine.server()))
    await asyncio.gather(*asyncio.all_tasks() - {asyncio.current_task()})


if __name__ == '__main__':
    asyncio.run(main())
