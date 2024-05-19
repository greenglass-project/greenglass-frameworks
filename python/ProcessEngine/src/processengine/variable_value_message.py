import json


class VariableValueMessage:
    def __init__(self, process_id, variable_id, type, value):
        self.process_id = process_id
        self.variable_id = variable_id
        self.value = value
        self.type = type

    def toJSON(self):
        return json.dumps(self, default=lambda o: o.__dict__, )
