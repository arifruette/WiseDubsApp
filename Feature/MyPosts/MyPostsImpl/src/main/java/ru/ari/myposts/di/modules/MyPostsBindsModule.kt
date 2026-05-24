package ru.ari.myposts.di.modules

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import ru.ari.myposts.domain.mapper.MyPostsUiMapper
import ru.ari.myposts.presentation.mappers.MyPostsUiMapperImpl
import ru.ari.myposts.presentation.viewmodel.MyPostsViewModelFactory

@Module
interface MyPostsBindsModule {

    @Binds
    fun bindMyPostsViewModelFactory(
        myPostsViewModelFactory: MyPostsViewModelFactory
    ): ViewModelProvider.Factory

    @Binds
    fun bindMyPostsUiMapper(
        myPostsUiMapperImpl: MyPostsUiMapperImpl
    ): MyPostsUiMapper
}
