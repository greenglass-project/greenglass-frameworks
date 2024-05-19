# ******************************************************************************
#  Copyright 2024 Greenglass Project
#
#  Use of this source code is governed by an MIT-style
#  license that can be found in the LICENSE file or at
#  https://opensource.org/licenses/MIT.
#
# *****************************************************************************/
import json


class VariableValueMessage:
    def __init__(self, process_id, variable_id, type, value):
        self.process_id = process_id
        self.variable_id = variable_id
        self.value = value
        self.type = type

    def toJSON(self):
        return json.dumps(self, default=lambda o: o.__dict__, )
