package ru.netology.nmedia.error

sealed class AppError(var code: String): RuntimeException()
class ApiError(val status: Int, code: String): Error(code)
object NetworkError : Error("error_network")
object UnknownError: Error("error_unknown")