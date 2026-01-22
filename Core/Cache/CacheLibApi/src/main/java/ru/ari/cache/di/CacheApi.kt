package ru.ari.cache.di

import ru.ari.cache.domain.datastore.DataStoreHelper

interface CacheApi {
    val dataStoreHelper: DataStoreHelper
}