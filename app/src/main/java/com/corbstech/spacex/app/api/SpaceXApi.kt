package com.corbstech.spacex.app.api

import com.corbstech.spacex.app.api.model.Company
import com.corbstech.spacex.app.api.model.LaunchData
import com.corbstech.spacex.app.api.model.LaunchDataRequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SpaceXApi {

    companion object {
        private const val API_VERSION = "v4"
        const val BASE_URL = "https://api.spacexdata.com/$API_VERSION/"
    }

    @POST("launches/query")
    suspend fun getLaunchData(
        @Body launchDataRequestBody: LaunchDataRequestBody
    ): Response<LaunchData>

    @GET("company")
    suspend fun getCompany(): Response<Company>
}