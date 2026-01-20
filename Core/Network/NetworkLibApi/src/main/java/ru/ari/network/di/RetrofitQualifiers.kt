package ru.ari.network.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class BaseRetrofit

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AuthRetrofit