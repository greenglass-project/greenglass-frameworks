package io.greenglass.host.control.system

import kotlinx.coroutines.*
import io.github.oshai.kotlinlogging.KotlinLogging
import io.greenglass.host.control.controlprocess.process.Process
import io.greenglass.host.control.controlsequence.Sequence

@OptIn(DelicateCoroutinesApi::class)
class SystemInstance(val instanceId : String,
                     val systemModel : SystemModel,
                     val processes : List<Process>,
                     val sequences : List<Sequence>
    ) {

    private val logger = KotlinLogging.logger {}

    @OptIn(ExperimentalCoroutinesApi::class)
    private val dispatcher = newSingleThreadContext("${systemModel.name}-$instanceId")

}
