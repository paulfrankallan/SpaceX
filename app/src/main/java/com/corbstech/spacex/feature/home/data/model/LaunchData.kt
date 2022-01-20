package com.corbstech.spacex.feature.home.data.model

import com.google.gson.annotations.SerializedName

data class LaunchData(
    @SerializedName("docs")
    val launches: List<Launch> = listOf(),
    val totalDocs: Int = 0,
    val offset: Int = 0,
    val limit: Int = 0,
    val totalPages: Int = 0,
    val page: Int = 0,
    val pagingCounter: Int = 0,
    val hasPrevPage: Boolean = false,
    val hasNextPage: Boolean = false,
    val prevPage: Int? = 0,
    val nextPage: Int? = 0,
)