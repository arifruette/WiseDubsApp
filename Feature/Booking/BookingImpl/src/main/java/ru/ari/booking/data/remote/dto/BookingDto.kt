package ru.ari.booking.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RoomResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("room_name") val roomName: String,
    @SerializedName("corpus") val corpus: String
)

data class GroupedRoomsResponse(
    @SerializedName("corpus") val corpus: String,
    @SerializedName("rooms") val rooms: List<RoomResponse>
)

data class BookingResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("time_start") val timeStart: String,
    @SerializedName("time_end") val timeEnd: String,
    @SerializedName("duration") val duration: Int,
    @SerializedName("description") val description: String? = null,
    @SerializedName("telegram_id") val telegramId: String,
    @SerializedName("published") val published: Boolean,
    @SerializedName("room_id") val roomId: Int,
    @SerializedName("room_name") val roomName: String,
    @SerializedName("message_id") val messageId: String? = null
)

data class CreateBookingRequest(
    @SerializedName("room_id") val roomId: Int,
    @SerializedName("time_start") val timeStart: String,
    @SerializedName("time_end") val timeEnd: String,
    @SerializedName("duration") val duration: Int,
    @SerializedName("description") val description: String? = null
)

data class UpdateBookingRequest(
    @SerializedName("room_id") val roomId: Int? = null,
    @SerializedName("time_start") val timeStart: String? = null,
    @SerializedName("time_end") val timeEnd: String? = null,
    @SerializedName("duration") val duration: Int? = null,
    @SerializedName("description") val description: String? = null
)
