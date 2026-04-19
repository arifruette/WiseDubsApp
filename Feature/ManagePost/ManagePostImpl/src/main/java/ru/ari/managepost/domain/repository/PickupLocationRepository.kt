package ru.ari.managepost.domain.repository

import ru.ari.posts.api.domain.models.CreatePickupLocationParams
import ru.ari.posts.api.domain.models.PickupLocation
import ru.ari.posts.api.domain.models.UpdatePickupLocationParams
import ru.ari.network.domain.models.Result

interface PickupLocationRepository {
    suspend fun getMyPickupLocations(): Result<List<PickupLocation>>
    suspend fun getPickupLocationById(id: Int): Result<PickupLocation>
    suspend fun createPickupLocation(params: CreatePickupLocationParams): Result<PickupLocation>
    suspend fun updatePickupLocation(id: Int, params: UpdatePickupLocationParams): Result<PickupLocation>
    suspend fun deletePickupLocation(id: Int): Result<Unit>
}
