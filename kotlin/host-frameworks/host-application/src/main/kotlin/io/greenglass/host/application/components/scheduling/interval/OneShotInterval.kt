package io.greenglass.host.application.components.scheduling.interval

import io.greenglass.host.application.components.scheduling.periodic.duration.ScheduleInstant
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.datetime.LocalDateTime
import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.*
import kotlin.collections.ArrayList

class OneShotIntervalScheduler() {

        private val logger = KotlinLogging.logger {}

        private val stateFlow = MutableSharedFlow<Boolean>(1)
        val scheduleFlow = MutableSharedFlow<LocalDateTime?>(0)

        fun subscribe() = stateFlow.asSharedFlow()

        var nextIndex = -1
        var duration = 0L
        private var currentTimer : TimerTask? = null

        var schedule : ArrayList<io.greenglass.host.application.components.scheduling.periodic.duration.ScheduleInstant> = arrayListOf()

    }