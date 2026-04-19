package ru.ari.managepost.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PickupLocationResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("corpus") val corpus: String? = null,
    @SerializedName("entrance") val entrance: String? = null,
    @SerializedName("floor") val floor: String? = null,
    @SerializedName("room") val room: String,
    @SerializedName("comment") val comment: String? = null,
    @SerializedName("label") val label: String? = null,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

data class CreatePickupLocationRequest(
    @SerializedName("corpus") val corpus: String? = null,
    @SerializedName("entrance") val entrance: String? = null,
    @SerializedName("floor") val floor: String? = null,
    @SerializedName("room") val room: String,
    @SerializedName("comment") val comment: String? = null,
    @SerializedName("label") val label: String? = null
)

data class UpdatePickupLocationRequest(
    @SerializedName("corpus") val corpus: String? = null,
    @SerializedName("entrance") val entrance: String? = null,
    @SerializedName("floor") val floor: String? = null,
    @SerializedName("room") val room: String? = null,
    @SerializedName("comment") val comment: String? = null,
    @SerializedName("label") val label: String? = null
)
