package io.greenglass.iot.host.registry

import io.greenglass.host.control.controlprocess.models.ProcessModel
import kotlin.reflect.KClass

//@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class RegisterProcess(val name : String, val model : KClass<out ProcessModel>)
