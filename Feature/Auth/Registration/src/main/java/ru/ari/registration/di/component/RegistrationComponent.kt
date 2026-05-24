package ru.ari.registration.di.component

import androidx.lifecycle.ViewModelProvider
import dagger.Component
import ru.ari.auth.common.api.di.AuthCommonApi
import ru.ari.registration.di.modules.RegistrationBindsModule
import ru.ari.registration.di.scope.RegistrationScope

@RegistrationScope
@Component(
    modules = [RegistrationBindsModule::class],
    dependencies = [AuthCommonApi::class]
)
interface RegistrationComponent {

    val registrationViewModelFactory: ViewModelProvider.Factory


    @Component.Factory
    interface Factory {
        fun create(
            authCommonApi: AuthCommonApi
        ): RegistrationComponent
    }
}