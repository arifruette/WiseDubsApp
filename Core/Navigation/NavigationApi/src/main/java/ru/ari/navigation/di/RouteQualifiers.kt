package ru.ari.navigation.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class PreLoginRoutes

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class PostLoginRoutes