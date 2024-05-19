# ******************************************************************************
#  Copyright 2024 Greenglass Project
#
#  Use of this source code is governed by an MIT-style
#  license that can be found in the LICENSE file or at
#  https://opensource.org/licenses/MIT.
#
# *****************************************************************************/
from abc import ABC, abstractmethod


class Process(ABC):
    @abstractmethod
    def run(self) -> None:
        pass
