package io.greenglass.host.control.system

import io.greenglass.host.control.controlprocess.models.ProcessModel
import io.greenglass.host.control.controlprocess.models.SequenceModel

class SystemModel(
    val systemId: String,
    val name: String,
    val description : String,
    val processes : List<ProcessModel>,
    val sequences : List<SequenceModel>
 ) {
    companion object
}


