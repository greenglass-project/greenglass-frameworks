# ******************************************************************************
#  Copyright 2024 Greenglass Project
#
#  Use of this source code is governed by an MIT-style
#  license that can be found in the LICENSE file or at
#  https://opensource.org/licenses/MIT.
#
# ***************************************************************************

import asyncio


class SyncVariableValue:
    """Synchronised variable value. Triggers an event when a variable is changed."""

    def __init__(self):
        self.event = asyncio.Event()
        self.value = None
        self.firstPass = True

    async def get_value(self):
        """Get the value of the variable. Suspends until the event is set."""
        await self.event.wait()
        value = self.value
        self.event.clear()
        return value

    async def set_value(self, value):
        """Set the value of the variable and trigger the event."""
        if self.firstPass:
            self.firstPass = False
        else:
            await self.event.wait()

        self.value = value
        self.event.set()
