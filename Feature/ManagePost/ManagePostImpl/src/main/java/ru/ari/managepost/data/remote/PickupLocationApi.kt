package ru.ari.managepost.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import ru.ari.managepost.data.remote.dto.CreatePickupLocationRequest
import ru.ari.managepost.data.remote.dto.PickupLocationResponse
import ru.ari.managepost.data.remote.dto.UpdatePickupLocationRequest

interface PickupLocationApi {

    @GET("pickup-locations/me")
    suspend fun getMyPickupLocations(): List<PickupLocationResponse>

    @GET("pickup-locations/{id}")
    suspend fun getPickupLocationById(@Path("id") id: Int): PickupLocationResponse

    @POST("pickup-locations")
    suspend fun createPickupLocation(@Body request: CreatePickupLocationRequest): PickupLocationResponse

    @PATCH("pickup-locations/{id}")
    suspend fun updatePickupLocation(
        @Path("id") id: Int,
        @Body request: UpdatePickupLocationRequest
    ): PickupLocationResponse

    @DELETE("pickup-locations/{id}")
    suspend fun deletePickupLocation(@Path("id") id: Int)
}
