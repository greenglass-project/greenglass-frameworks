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
        self.config_root = variable_or_default("GREENGLASS_CONFIG_ROOT", "config")
        self.engine_host = variable_or_default("GREENGLASS_ENGINE_HOST", "localhost")
        self.engine_port = variable_or_default("GREENGLASS_ENGINE_PORT", 8282)

        for file in os.listdir(self.config_root + "/systems"):
            if file.endswith(".yaml"):
                with open(self.config_root + "/systems/" + file, "r") as f:
                    data = yaml.full_load(f)
                    self.processes = data["processes"]
            break
