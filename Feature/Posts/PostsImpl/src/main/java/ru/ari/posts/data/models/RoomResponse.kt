package ru.ari.posts.data.models

import com.google.gson.annotations.SerializedName

data class RoomResponse(
    val id: Long,
    @SerializedName("room_name")
    val roomName: String,
    val corpus: String
)
