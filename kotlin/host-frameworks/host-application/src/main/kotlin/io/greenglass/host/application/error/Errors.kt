/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.host.application.error

import io.github.oshai.kotlinlogging.KotlinLogging
import io.greenglass.host.application.error.ErrorCodes.Companion.ILLEGAL_STATE_CHANGE
import io.greenglass.host.application.error.ErrorCodes.Companion.INTERNAL_ERROR
import io.greenglass.host.application.error.ErrorCodes.Companion.OBJECT_ALREADY_EXISTS
import io.greenglass.host.application.error.ErrorCodes.Companion.OBJECT_IN_USE
import io.greenglass.host.application.error.ErrorCodes.Companion.OBJECT_NOT_AVAILABLE
import io.greenglass.host.application.error.ErrorCodes.Companion.OBJECT_NOT_FOUND
import io.greenglass.host.application.error.ErrorCodes.Companion.TYPE_MISMATCH
import io.greenglass.host.application.microservice.nats.logger
import kotlin.system.exitProcess

/**
 * ErrorCodes
 *
 */
class ErrorCodes {
    companion object {
        const val INTERNAL_ERROR = "INTERNAL_ERROR"
        const val OBJECT_ALREADY_EXISTS = "OBJECT_ALREADY_EXISTS"
        const val OBJECT_NOT_FOUND = "OBJECT_NOT_FOUND"
        const val OBJECT_NOT_AVAILABLE = "OBJECT_NOT_AVAILABLE"
        const val ILLEGAL_STATE_CHANGE = "ILLEGAL_STATE_CHANGE"
        const val OBJECT_IN_USE = "OBJECT_IN_USE"
        const val TYPE_MISMATCH = "TYPE_MISMATCH"
    }
}

// ===================================================================================
// Standard Exceptions
// ===================================================================================
open class FailureException(val code : String, val param : String) : Exception("[$code] = $param")
class InternalErrorException(param : String) : FailureException(INTERNAL_ERROR, param)
class NotFoundException(param : String) : FailureException(OBJECT_NOT_FOUND, param)
class NotAvailableException(param : String): FailureException(OBJECT_NOT_AVAILABLE, param)
class AlreadyExistsException(param : String) : FailureException(OBJECT_ALREADY_EXISTS,param)
class InuseException(param : String) : FailureException(OBJECT_IN_USE, param)
class TypeMismatchException(param : String) : FailureException(TYPE_MISMATCH,param)
class IllegalStateChangeException(param : String) : FailureException(ILLEGAL_STATE_CHANGE,param)


// ===================================================================================
// Error helper functions
// ===================================================================================

/************************************************************************************
 * checkExists
 * If the value is not null return the value otherwise
 * throw a NotFoundException exception
 *
 * @param T the type of the object
 * @param objId and id to identify the object in the exception
 * @param obj the object to check
 * @return the not-null object
 *************************************************************************************/
@Throws(NotFoundException::class)
fun <T>checkExists(objId : String,obj : T?) : T {
    if (obj != null)
        return obj
    else
        throw NotFoundException(objId)
}
/************************************************************************************
 * checkNotExists
 * If the value is not null throw an AlreadyExistsException
 *
 * @param T the type of the object
 * @param objId and id to identify the object in the exception
 * @param obj
 * @return
 *************************************************************************************/
@Throws(AlreadyExistsException::class)
fun <T>checkNotExists(objId : String, obj : T?){
    if (obj != null)
        throw AlreadyExistsException(objId)
}

/************************************************************************************
 * If the value is not null return the value otherwise
 * throw a NotAvailableException exception
 *
 * @param T the type of the object
 * @param objId and id to identify the object in the exception
 * @param obj the object to check
 * @return the not-null object
 *************************************************************************************/
@Throws(NotAvailableException::class)
fun <T>checkAvailable(objId : String, obj : T?) : T {
    if (obj != null)
        return obj
    else
        throw NotAvailableException(objId)
}

/************************************************************************************
 * exceptionToFailure
 * Wraps a block of code to catch any exception and return a Failure Result.
 * The code block must return the normal case a Result, so the function returns either
 * this Result (which can be a failure), or the Failure from an exception
 *
 * @param T the type returned in a Success Result
 * @param block the code block
 * @return the overall Result
 *************************************************************************************/
fun <T : Any> exceptionToFailure(block : () -> Result<T>) : Result<T> {
    val logger = KotlinLogging.logger {}
    return try{
        block()
    } catch(ex : FailureException) {
        logger.debug {  "ERROR ${ex.code} ${ex.param}"}
        Result.Failure(ex.code, ex.param)
    } catch(ex : Exception) {
        logger.error { ex.stackTraceToString() }
        Result.Failure(INTERNAL_ERROR, ex.message ?: "")
    }
}

fun exceptionToBoolean(block : () -> Boolean) : Boolean {
    return try{
        block()
    } catch(ex : FailureException) {
        false
    } catch(ex : Exception) {
        KotlinLogging.logger {}
            .error { ex.stackTraceToString() }
        false
    }
}

fun exceptionToFatal(block : () -> Unit) {
    val logger = KotlinLogging.logger {}
    return try {
        block()
    } catch(ex : FailureException) {
        logger.error { "FATAL ERROR ${ex.code} ${ex.param}" }
        exitProcess(1)
    } catch(ex : Exception) {
        logger.error { "FATAL ERROR ${ex.stackTraceToString()}" }
        exitProcess(1)
    }
}
