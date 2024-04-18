import io.greenglass.host.application.error.*

@Throws(FailureException::class)
inline fun <reified T> checkSuccess(result : Result<T>) : T {
    if(result is Result.Success<*> && result.value is T) {
        return result.value as T
    } else if( result is Result.Failure<*>){
        throw FailureException(result.code, result.msg)
    } else throw TypeMismatchException()
}

inline fun <reified T> checkSuccessOrNull(result : Result<T>) : T? {
    return if(result is Result.Success<*> && result.value is T)
        result.value as T
    else
        null
}

@Throws(NotFoundException::class)
fun <T>checkExists(obj : T?) : T {
    if (obj != null)
        return obj
    else
        throw NotFoundException()
}
@Throws(AlreadyExistsException::class)
fun <T>checkNotExists(obj : T?){
    if (obj != null)
        throw AlreadyExistsException()
}
@Throws(NotAvailableException::class)
fun <T>checkAvailable(obj : T?) : T {
    if (obj != null)
        return obj
    else
        throw NotAvailableException()
}

fun <T : Any>checkNullSuccessOrNotFoundError(objId : String, obj : T?, ) : Result<T> {
    return if(obj != null)
        Result.Success(obj)
    else
        Result.Failure(ErrorCodes.OBJECT_NOT_FOUND, objId)
}

