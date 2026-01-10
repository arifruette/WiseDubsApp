package ru.ari.cache.di

import ru.ari.cache.datastore.DataStoreHelper

interface DataStoreDeps {
    val dataStoreHelper: DataStoreHelper
}