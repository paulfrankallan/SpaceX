package com.corbstech.spacex.feature.home.data

import com.corbstech.spacex.feature.home.api.LaunchDataRequestBody
import com.corbstech.spacex.feature.home.api.Options
import com.corbstech.spacex.feature.home.api.Query
import com.corbstech.spacex.feature.home.api.SpaceXApi
import javax.inject.Inject

open class SpaceXRepository @Inject constructor(
    private var spaceXApi: SpaceXApi
) {
    suspend fun getCompany() =
        spaceXApi.getCompany()

    suspend fun getLaunchData() =
        spaceXApi.getLaunchData(LaunchDataRequestBody(Options(), Query))
}
