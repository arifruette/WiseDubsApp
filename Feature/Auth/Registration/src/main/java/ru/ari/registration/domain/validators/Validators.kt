package ru.ari.registration.domain.validators

fun validateUserEmail(userEmail: String): EmailError {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    return if (userEmail.matches(emailRegex.toRegex())) {
        EmailError.NO_ERROR
    } else EmailError.EMAIL_FORMAT
}

fun validateUserPassword(userPassword: String): PasswordError {
    if (userPassword.length !in 5..50) {
        return PasswordError.PASSWORD_LENGTH
    }
    return PasswordError.NO_ERROR
}

fun validateUserPasswordsEquality(
    firstUserPasswordText: String,
    secondUserPasswordText: String
): PasswordError {
    if (firstUserPasswordText != secondUserPasswordText) {
        return PasswordError.PASSWORDS_DIFFERENT
    }
    return PasswordError.NO_ERROR
}

enum class PasswordError(val message: String?) {
    PASSWORD_LENGTH("Пароль должен быть длиной от 5 до 50 символов"),
    PASSWORDS_DIFFERENT("Пароли должны совпадать"),
    NO_ERROR(null)
}

enum class EmailError(val message: String?) {
    EMAIL_FORMAT("Неверный формат почты"),
    NO_ERROR(null)
}