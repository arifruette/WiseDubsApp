package ru.ari.managepost.data.repository

import retrofit2.HttpException
import ru.ari.managepost.data.mappers.toDomain
import ru.ari.managepost.data.remote.PickupLocationApi
import ru.ari.managepost.data.remote.dto.CreatePickupLocationRequest
import ru.ari.managepost.data.remote.dto.UpdatePickupLocationRequest
import ru.ari.posts.api.domain.models.CreatePickupLocationParams
import ru.ari.posts.api.domain.models.PickupLocation
import ru.ari.posts.api.domain.models.UpdatePickupLocationParams
import ru.ari.managepost.domain.repository.PickupLocationRepository
import ru.ari.network.domain.models.Result
import javax.inject.Inject

class PickupLocationRepositoryImpl @Inject constructor(
    private val api: PickupLocationApi
) : PickupLocationRepository {

    override suspend fun getMyPickupLocations(): Result<List<PickupLocation>> = try {
        Result.Success(api.getMyPickupLocations().map { it.toDomain() })
    } catch (e: HttpException) {
        Result.Error(e.code(), e.message())
    } catch (e: Throwable) {
        Result.Exception(e)
    }

    override suspend fun getPickupLocationById(id: Int): Result<PickupLocation> = try {
        Result.Success(api.getPickupLocationById(id).toDomain())
    } catch (e: HttpException) {
        Result.Error(e.code(), e.message())
    } catch (e: Throwable) {
        Result.Exception(e)
    }

    override suspend fun createPickupLocation(params: CreatePickupLocationParams): Result<PickupLocation> = try {
        val request = CreatePickupLocationRequest(
            corpus = params.corpus,
            entrance = params.entrance,
            floor = params.floor,
            room = params.room,
            comment = params.comment,
            displayText = params.displayText
        )
        Result.Success(api.createPickupLocation(request).toDomain())
    } catch (e: HttpException) {
        Result.Error(e.code(), e.message())
    } catch (e: Throwable) {
        Result.Exception(e)
    }

    override suspend fun updatePickupLocation(id: Int, params: UpdatePickupLocationParams): Result<PickupLocation> =
        try {
            val request = UpdatePickupLocationRequest(
                corpus = params.corpus,
                entrance = params.entrance,
                floor = params.floor,
                room = params.room,
                comment = params.comment,
                displayText = params.displayText
            )
            Result.Success(api.updatePickupLocation(id, request).toDomain())
        } catch (e: HttpException) {
            Result.Error(e.code(), e.message())
        } catch (e: Throwable) {
            Result.Exception(e)
        }

    override suspend fun deletePickupLocation(id: Int): Result<Unit> = try {
        api.deletePickupLocation(id)
        Result.Success(Unit)
    } catch (e: HttpException) {
        Result.Error(e.code(), e.message())
    } catch (e: Throwable) {
        Result.Exception(e)
    }
}
