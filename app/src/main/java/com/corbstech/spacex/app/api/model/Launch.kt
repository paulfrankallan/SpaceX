package com.corbstech.spacex.app.api.model

import com.google.gson.annotations.SerializedName

data class Launch(
    @SerializedName("flight_number") val flightNumber: Int?,
    @SerializedName("date_unix") val dateUnix: Long?,
    @SerializedName("date_utc") val dateUtc: String?,
    @SerializedName("date_local") val dateLocal: String?,
    @SerializedName("success") val success: Boolean? = null,
    @SerializedName("links") val links: Links?,
    @SerializedName("details") val details: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("rocket") val rocket: Rocket?,
    @SerializedName("id") val id: String,
)

data class Rocket(
    @SerializedName("name") val name: String?,
    @SerializedName("type") val type: String?
)

data class Links(
    @SerializedName("patch") val patch: Patch?,
    @SerializedName("article") val articleLink: String?,
    @SerializedName("youtube_id") val youTubeId: String?,
    @SerializedName("webcast") val webcast: String?,
    @SerializedName("wikipedia") val wikipedia: String?
)

data class Patch(
    @SerializedName("small") var small: String?,
    @SerializedName("large") val large: String?,
)
