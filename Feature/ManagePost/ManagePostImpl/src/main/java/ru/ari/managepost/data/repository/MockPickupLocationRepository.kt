package ru.ari.managepost.data.repository

import javax.inject.Inject
import ru.ari.managepost.di.scope.ManagePostScreenScope
import ru.ari.managepost.domain.repository.PickupLocationRepository
import ru.ari.network.domain.models.Result
import ru.ari.posts.api.domain.models.CreatePickupLocationParams
import ru.ari.posts.api.domain.models.MockPickupLocationStore
import ru.ari.posts.api.domain.models.PickupLocation
import ru.ari.posts.api.domain.models.UpdatePickupLocationParams

@ManagePostScreenScope
class MockPickupLocationRepository @Inject constructor() : PickupLocationRepository {

    override suspend fun getMyPickupLocations(): Result<List<PickupLocation>> {
        return Result.Success(MockPickupLocationStore.getAll())
    }

    override suspend fun getPickupLocationById(id: Int): Result<PickupLocation> {
        val location = MockPickupLocationStore.getById(id)
        return if (location != null) {
            Result.Success(location)
        } else {
            Result.Error(404, "Location not found")
        }
    }

    override suspend fun createPickupLocation(params: CreatePickupLocationParams): Result<PickupLocation> {
        val newLocation = MockPickupLocationStore.create(
            PickupLocation(
                id = 0,
                userId = 1,
                corpus = params.corpus,
                entrance = params.entrance,
                floor = params.floor,
                room = params.room,
                comment = params.comment,
                label = params.label?.takeIf(String::isNotBlank),
                createdAt = "2023-10-25T15:00:00Z",
                updatedAt = "2023-10-25T15:00:00Z"
            )
        )
        return Result.Success(newLocation)
    }

    override suspend fun updatePickupLocation(id: Int, params: UpdatePickupLocationParams): Result<PickupLocation> {
        val updated = MockPickupLocationStore.update(id) { old ->
            val room = params.room ?: old.room
            old.copy(
                corpus = params.corpus,
                entrance = params.entrance,
                floor = params.floor,
                room = room,
                comment = params.comment,
                label = params.label?.takeIf(String::isNotBlank),
                updatedAt = "2023-10-25T16:00:00Z"
            )
        } ?: return Result.Error(404, "Location not found")

        return Result.Success(updated)
    }

    override suspend fun deletePickupLocation(id: Int): Result<Unit> {
        MockPickupLocationStore.delete(id)
        return Result.Success(Unit)
    }
}
