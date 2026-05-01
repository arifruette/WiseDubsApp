package ru.ari.profile.di.component

import dagger.Component
import ru.ari.auth.common.api.di.AuthCommonApi
import ru.ari.posts.api.di.PostsApi
import ru.ari.profile.di.modules.ProfileBindsModule
import ru.ari.profile.di.scope.ProfileScreenScope
import ru.ari.profile.presentation.viewmodel.ProfileViewModelFactory
import ru.ari.profile.presentation.viewmodel.ReservedPostsViewModelFactory
import ru.ari.sharingpostdetails.api.di.SharingPostDetailsApi

@ProfileScreenScope
@Component(
    modules = [ProfileBindsModule::class],
    dependencies = [
        AuthCommonApi::class,
        PostsApi::class,
        SharingPostDetailsApi::class
    ]
)
interface ProfileComponent {

    val profileViewModelFactory: ProfileViewModelFactory
    val reservedPostsViewModelFactory: ReservedPostsViewModelFactory

    @Component.Factory
    interface Factory {
        fun create(
            authCommonApi: AuthCommonApi,
            postsApi: PostsApi,
            sharingPostDetailsApi: SharingPostDetailsApi
        ): ProfileComponent
    }
}
