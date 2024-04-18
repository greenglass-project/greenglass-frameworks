package io.greenglass.host.application.error

class FailureException(code : String, msg : String) : Exception("[$code] = $msg")
class NotFoundException : Exception("Not Found")
class NotAvailableException : Exception("Not Available")
class AlreadyExistsException : Exception("Already exists")
class InuseException : Exception("In use")
class TypeMismatchException : Exception("Type mismatch")

