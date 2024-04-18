package io.greenglass.host.application.components.scheduling.periodic.duration

import kotlinx.datetime.LocalTime

class DailyDurationEvent(val start : LocalTime, val end : LocalTime)
