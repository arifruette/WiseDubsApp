package ru.ari.booking.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import ru.ari.booking.data.remote.dto.BookingResponse
import ru.ari.booking.data.remote.dto.CreateBookingRequest
import ru.ari.booking.data.remote.dto.GroupedRoomsResponse
import ru.ari.booking.data.remote.dto.UpdateBookingRequest

interface BookingRemoteApi {
    @GET("rooms/grouped")
    suspend fun getGroupedRooms(): List<GroupedRoomsResponse>

    @GET("booking/posts")
    suspend fun getBookings(
        @Query("room_id") roomId: Int,
        @Query("time_start") timeStart: String,
        @Query("time_end") timeEnd: String
    ): List<BookingResponse>

    @POST("booking/posts")
    suspend fun createBooking(@Body request: CreateBookingRequest): BookingResponse

    @GET("booking/posts/me")
    suspend fun getMyBookings(): List<BookingResponse>

    @PATCH("booking/posts/{post_id}")
    suspend fun updateBooking(
        @Path("post_id") postId: Long,
        @Body request: UpdateBookingRequest
    ): BookingResponse

    @DELETE("booking/posts/{post_id}")
    suspend fun deleteBooking(@Path("post_id") postId: Long)
}
