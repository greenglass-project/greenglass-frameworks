package io.greenglass.host.control.controlsequence

import io.greenglass.host.control.system.SystemInstance
import io.greenglass.host.control.controlprocess.models.SequenceModel
import io.greenglass.host.control.controlprocess.providers.MetricProvider
import io.greenglass.host.control.controlprocess.providers.SequenceStateProvider
import io.greenglass.host.sparkplug.SparkplugService

class Sequence(val systemInstance : SystemInstance,
               val sequenceModel : SequenceModel,
               val metricProvider : MetricProvider,
               val sequenceStateProvider : SequenceStateProvider,
               val sparkplugService: SparkplugService,
)