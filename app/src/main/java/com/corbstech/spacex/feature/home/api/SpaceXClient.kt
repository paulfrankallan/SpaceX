package com.corbstech.spacex.feature.home.api

import android.content.Context
import com.corbstech.spacex.feature.home.api.SpaceXApi.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SpaceXClient(context: Context) {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    setLevel(HttpLoggingInterceptor.Level.BODY)
                })
//                .addInterceptor(NoConnectionInterceptor(context))
                .build()
        ).build()

    val api: SpaceXApi by lazy {
        retrofit.create(
            SpaceXApi::class.java
        )
    }
}