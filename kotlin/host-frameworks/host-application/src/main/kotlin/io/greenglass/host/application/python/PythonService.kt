package io.greenglass.host.application.python

import org.jpy.PyLib

class PythonService {

    init {
        PyLib.startPython()
        if(!PyLib.isPythonRunning())
            throw IllegalStateException("Python is not running or not correctly installed")
    }
}