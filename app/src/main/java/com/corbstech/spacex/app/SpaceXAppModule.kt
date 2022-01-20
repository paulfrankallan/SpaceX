package com.corbstech.spacex.app

import android.content.Context
import com.corbstech.spacex.feature.home.api.SpaceXClient
import com.corbstech.spacex.shared.DefaultResourceProvider
import com.corbstech.spacex.shared.ResourceProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers

@InstallIn(ViewModelComponent::class)
@Module
class SpaceXAppModule {

    @Provides
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    fun providesWeatherAppClient(
        @ApplicationContext context: Context
    ) = SpaceXClient(context).api

    @Provides
    fun providesDispatcher() = Dispatchers.IO

    @Provides
    fun providesResourceProvider(
        @ApplicationContext context: Context
    ): ResourceProvider = DefaultResourceProvider(context)
}