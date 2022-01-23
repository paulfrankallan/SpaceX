package com.corbstech.spacex.app.api.model

import com.google.gson.annotations.SerializedName

data class LaunchData(
    @SerializedName("docs")
    val launches: List<Launch> = listOf()
)