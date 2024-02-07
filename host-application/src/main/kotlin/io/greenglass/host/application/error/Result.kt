package io.greenglass.host.application.error

sealed class Result<T> {
    data class Success<T : Any>(val value: T) : Result<T>()
    data class Failure<T>(val code: String, val msg: String) : Result<T>()

    /*inline fun <reified T> onSuccess(func: (T)->Unit) : Result<T> {
        if (this is Success<*>) {
            if (this.value is T)
                func(this.value as T)
        }
        return this
    }*/

    /*fun successOrFailureException() : Success {
        if(this is Result.Success) {
            return this.value
        } else {
            this as Result.Failure
            throw FailureException(this.reason.code, this.reason.msg)
        }
    }

    fun successOrError() : Success {
        if(this is Result.Success) {
            return this.value
        } else {
            this as Result.Failure
            throw FailureException(this.reason.code, this.reason.msg)
        }
    }*/

    inline fun <reified T> successOrNull(): T? {
        return if (this is Success<*> && value is T)
            value
        else
            null
    }
}




