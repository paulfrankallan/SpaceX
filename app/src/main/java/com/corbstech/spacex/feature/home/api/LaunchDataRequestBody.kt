package com.corbstech.spacex.feature.home.api

data class LaunchDataRequestBody(
    val options: Options,
    val query: Query
)

object Query

data class Options(
    val limit: Int? = null,
    val pagination: Boolean = false,
    val populate: List<String> = listOf("rocket")
)