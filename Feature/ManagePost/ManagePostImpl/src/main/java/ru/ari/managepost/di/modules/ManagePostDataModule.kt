package ru.ari.managepost.di.modules

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import ru.ari.managepost.data.remote.PickupLocationApi
import ru.ari.network.di.AuthRetrofit

@Module
class ManagePostDataModule {

    @Provides
    fun providePickupLocationApi(@AuthRetrofit retrofit: Retrofit): PickupLocationApi {
        return retrofit.create(PickupLocationApi::class.java)
    }
}
