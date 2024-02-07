package io.greenglass.host.application.components.scheduling.periodic.duration

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.SharedFlow
import org.jetbrains.annotations.TestOnly
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timerTask
import kotlin.time.DurationUnit
import kotlin.time.toDuration

data class ScheduleInstant(val timeStamp : LocalTime, val state : Boolean)

@OptIn(DelicateCoroutinesApi::class)
class DailyDurationScheduler(private val instanceId : String,
                             val schedulerId : String,
                             private val scheduleFlow : SharedFlow<List<io.greenglass.host.application.components.scheduling.periodic.duration.DailyDurationEvent>>
) {

    private val logger = KotlinLogging.logger {}
    private val stateFlow = MutableSharedFlow<Boolean>(1)

    var nextIndex = -1
    var duration = 0L
    private var currentTimer : TimerTask? = null

    var schedule : ArrayList<io.greenglass.host.application.components.scheduling.periodic.duration.ScheduleInstant> = arrayListOf()

    fun subscribeToState() = stateFlow.asSharedFlow()

    init {
        logger.debug { "DailyDurationScheduler $schedulerId started"  }
        GlobalScope.launch {
            scheduleFlow.collect { s -> changeSchedule(s) }
        }
    }

    private suspend fun changeSchedule(events : List<io.greenglass.host.application.components.scheduling.periodic.duration.DailyDurationEvent>) {

        logger.debug {" changeSchedule() no-of-events = ${events.size}" }
        // If there is an active timer cancel it
        if(currentTimer != null) {
            logger.debug {" Cancelling current timer" }
            currentTimer!!.cancel()
            currentTimer = null
        }

        var currentState = false
        // If there is currently a schedule find its current state
        // (which is the inverse of the next state)
        if(nextIndex != -1) {
            val next = schedule[nextIndex]
            currentState = !next.state
            logger.debug {" Current state = $currentState" }
        }

        // If there are no events cancel the current timer, reset the index and duration,
        // and set the current state to off (false)
        if(events.isEmpty()) {
            logger.debug {" Event list is empty" }
            if(currentState) {
                logger.debug {" Event list is empty and current state is on - turn it off" }
                stateFlow.emit(false)
            }
            nextIndex = -1
            duration = 0L

        } else {
            logger.debug {"Creating the schedule" }
            schedule.clear()
            for (event in events) {
                schedule.add(
                    io.greenglass.host.application.components.scheduling.periodic.duration.ScheduleInstant(
                        event.start,
                        true
                    )
                )
                schedule.add(
                    io.greenglass.host.application.components.scheduling.periodic.duration.ScheduleInstant(
                        event.end,
                        false
                    )
                )
            }
            initialEvent()
        }
    }

    private fun nextEvent() {
        logger.debug {" nextEvent()" }
        val nowInstant = Clock.System.now()
        val now = nowInstant.toLocalDateTime(TimeZone.currentSystemDefault())
        val nowDate = now.date
        val inst : Instant
        if(nextIndex == schedule.size-1) {

            nextIndex = 0
            inst = LocalDateTime(nowDate, schedule[nextIndex].timeStamp)
                .toInstant(TimeZone.currentSystemDefault())
                .plus(1.toDuration(DurationUnit.DAYS))
        } else {
            ++nextIndex
            inst = LocalDateTime(nowDate, schedule[nextIndex].timeStamp).toInstant(TimeZone.currentSystemDefault())
        }
        duration = inst.minus(nowInstant).inWholeMilliseconds
        logger.debug {"nextIndex = $nextIndex duration = $duration" }
    }

    private fun initialEvent() {
        logger.debug {" initialEvent()" }

        val nowInstant = Clock.System.now()
        val now = nowInstant.toLocalDateTime(TimeZone.currentSystemDefault())
        val nowTime = now.time
        val nowDate = now.date

        val next = schedule.withIndex().firstOrNull { i -> i.value.timeStamp > nowTime }
        if(next != null) {
            nextIndex = next.index

            val inst = LocalDateTime(nowDate, next.value.timeStamp).toInstant(TimeZone.currentSystemDefault())
            duration = inst.minus(nowInstant).inWholeMilliseconds
        } else {
            val first = schedule.first()
            val inst = LocalDateTime(nowDate, first.timeStamp)
                .toInstant(TimeZone.currentSystemDefault())
                .plus(1.toDuration(DurationUnit.DAYS))
            duration = inst.minus(nowInstant).inWholeMilliseconds
            nextIndex = 0
        }
        logger.debug {"nextIndex = $nextIndex duration = $duration" }
        val initialState = !schedule[nextIndex].state

        logger.debug {"Setting initial state = $initialState" }
        stateFlow.tryEmit(initialState)
        startTimer()
    }

    fun startTimer() {
        logger.debug {" startTimer() duration = $duration" }
        currentTimer = timerTask {
            val event = schedule[nextIndex]
            stateFlow.tryEmit(event.state)
            logger.debug {"Timer fired event = $event" }
            nextEvent()
            startTimer()
        }

        Timer().schedule(currentTimer, duration)
    }

    @TestOnly
    fun testChangeSchedule(events : List<io.greenglass.host.application.components.scheduling.periodic.duration.DailyDurationEvent>) {
        runBlocking {
            changeSchedule(events)
        }
    }
}