package ru.ari.cache.di

import ru.ari.cache.domain.SharingPostDataSource
import ru.ari.cache.domain.datastore.DataStoreHelper

interface CacheApi {
    val dataStoreHelper: DataStoreHelper
    val sharingPostDataSource: SharingPostDataSource
}