package ru.ari.cache.di

import ru.ari.cache.datastore.DataStoreHelper

interface CacheDeps {
    val dataStoreHelper: DataStoreHelper
}