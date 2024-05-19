import yaml
import os


def variable_or_default(name, default):
    """Read an environment variable or return default value."""
    try:
        return os.environ[name]
    except:
        return default


class Configuration:
    """Read configuration from environment files and the system description YAML file."""

    def __init__(self):
        self.config_root = variable_or_default("CONFIG_ROOT", "config")
        self.engine_host = variable_or_default("ENGINE_HOSTNAME", "localhost")
        self.engine_port = variable_or_default("ENGINE_WEBSOCKET_PORT", 9595)

        print("CONFIG_ROOT = " + self.config_root)
        print("ENGINE_HOST = " + self.engine_host)
        print("ENGINE_PORT = " + self.engine_port)

        for file in os.listdir(self.config_root + "/systems"):
            if file.endswith(".yaml"):
                with open(self.config_root + "/systems/" + file, "r") as f:
                    data = yaml.full_load(f)
                    self.processes = data["processes"]
            break
