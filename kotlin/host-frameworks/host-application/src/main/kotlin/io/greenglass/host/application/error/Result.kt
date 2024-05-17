/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.host.application.error

import io.greenglass.host.application.microservice.nats.logger

/************************************************************************************
 * Result
 * Standard Greenglass Result as asealed class comprising
 * Success - containing the value
 * Error - containing an error code and error parameter
 *
 * @param T the type returned when successful
 *************************************************************************************/
sealed class Result<T> {

    /**********************************************************************************
     * Success
     * The Success case
     * @param T the type returned
     * @property value the value
     **********************************************************************************/
    data class Success<T : Any>(val value: T) : Result<T>()

    /**********************************************************************************
     * Failure
     * The Failure case
     * @param T unused
     * @property code an error code
     * @property param an error parameter
     **********************************************************************************/
    data class Failure<T>(val code: String, val param: String) : Result<T>()

    /**********************************************************************************
     * valueOrNull
     *
     * Return the value is Success, or a null if failure
     * @param T the value's type
     * @return the value or null
     **********************************************************************************/
    inline fun <reified T> valueOrNull(): T? =
         if (this is Success<*> && value is T) value else null

    /**********************************************************************************
     * valueOrException
     *
     * Return the value if Success or throw the corresponding
     * exception if Failure
     * @param T the value's type
     * @return the value if Success
     **********************************************************************************/
    inline fun  <reified T> valueOrException(): T {
        if (this is Success<*> && value is T)
            return value
        else {
            val f = this as Failure
            logger.debug {  "ERROR ${f.code} ${f.param}"}
            throw FailureException(f.code, f.param)
        }
    }

    /**********************************************************************************
     * valueOrDefault
     *
     * Return the value if Success or a default value if Failure
     *
     * @param T the value's type
     * @param default the default
     * @return either the value or the default
     **********************************************************************************/
    inline fun <reified T> valueOrDefault(default : T) : T =
        if (this is Success<*> && value is T) value else default

    /**********************************************************************************
     * route
     *
     * Call either a user supplied success function oe failur function according to the Result
     *
     * @param T the value's type
     * @param success the function to be called if Success
     * @param orElse the function to be called if Failure
     **********************************************************************************/
    inline fun <reified T> route(success : (T) -> Unit, orElse : (String,String) -> Unit) {
        if (this is Success<*> && value is T)
            success(this.value as T)
        else {
            this as Failure
            orElse(this.code, this.param)
        }
    }
}




