package ru.ari.registration.domain.usecase

import ru.ari.registration.domain.validators.EmailError
import ru.ari.registration.domain.validators.PasswordError
import ru.ari.registration.domain.validators.validateUserEmail
import ru.ari.registration.domain.validators.validateUserPassword
import ru.ari.registration.domain.validators.validateUserPasswordsEquality
import javax.inject.Inject

class ValidateUserCredentialsUseCase @Inject constructor(){
    suspend operator fun invoke(
        email: String,
        firstPassword: String,
        secondPassword: String,
        onError: suspend (String?) -> Unit
    ): Boolean {
        if (validateUserEmail(email) == EmailError.EMAIL_FORMAT) {
            onError(EmailError.EMAIL_FORMAT.message)
            return false
        }
        if (validateUserPasswordsEquality(
                firstPassword,
                secondPassword
            ) == PasswordError.PASSWORDS_DIFFERENT
        ) {
            onError(PasswordError.PASSWORDS_DIFFERENT.message)
            return false
        }
        if (validateUserPassword(firstPassword) == PasswordError.PASSWORD_LENGTH) {
            onError(PasswordError.PASSWORD_LENGTH.message)
            return false
        }
        return true
    }
}