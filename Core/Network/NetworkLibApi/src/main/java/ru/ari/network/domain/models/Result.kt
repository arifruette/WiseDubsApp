package ru.ari.network.domain.models

import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class Result<out T> {
    class Success<out T>(val data: T): Result<T>()
    class Error(val code: Int, message: String): Result<Nothing>() {
        val message: String = message.toUserErrorMessage()
    }
    class Exception(val error: Throwable): Result<Nothing>()
}

fun String?.toUserErrorMessage(defaultMessage: String = UNKNOWN_ERROR_MESSAGE): String {
    val message = this?.trim().orEmpty()
    return when {
        message.isBlank() -> defaultMessage
        message.containsLatinLetters() -> defaultMessage
        else -> message
    }
}

fun Throwable.toUserErrorMessage(defaultMessage: String = UNKNOWN_ERROR_MESSAGE): String =
    if (isNetworkError()) {
        NO_INTERNET_ERROR_MESSAGE
    } else {
        message.toUserErrorMessage(defaultMessage)
    }

private fun Throwable.isNetworkError(): Boolean =
    this is UnknownHostException ||
        this is ConnectException ||
        this is SocketException ||
        this is SocketTimeoutException

private fun String.containsLatinLetters(): Boolean =
    any { it in 'A'..'Z' || it in 'a'..'z' }

private const val UNKNOWN_ERROR_MESSAGE = "Неизвестная ошибка"
private const val NO_INTERNET_ERROR_MESSAGE = "Нет подключения к интернету. Проверьте подключение и попробуйте снова"

inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}

inline fun <T> Result<T>.onError(action: (code: Int, message: String) -> Unit): Result<T> {
    if (this is Result.Error) action(code, message)
    return this
}

inline fun <T> Result<T>.onException(action: (Throwable) -> Unit): Result<T> {
    if (this is Result.Exception) action(error)
    return this
}

inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> {
    return when (this) {
        is Result.Success -> Result.Success(transform(data))
        is Result.Error -> Result.Error(code, message)
        is Result.Exception -> Result.Exception(error)
    }
}
