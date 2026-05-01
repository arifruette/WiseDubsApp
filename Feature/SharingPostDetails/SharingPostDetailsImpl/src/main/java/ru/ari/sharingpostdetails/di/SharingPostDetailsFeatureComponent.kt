package ru.ari.sharingpostdetails.di

import dagger.Component
import ru.ari.sharingpostdetails.api.di.SharingPostDetailsApi
import ru.ari.sharingpostdetails.di.modules.SharingPostDetailsApiModule
import javax.inject.Singleton

@Singleton
@Component(modules = [SharingPostDetailsApiModule::class])
interface SharingPostDetailsFeatureComponent : SharingPostDetailsApi
