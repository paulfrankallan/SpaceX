package com.corbstech.spacex.shared

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.corbstech.spacex.feature.home.api.SpaceXApi
import com.corbstech.spacex.feature.home.list.launch.LaunchItemLink
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SpaceXViewModelTest {

    // Test subject
    private lateinit var spaceXViewModel: SpaceXViewModel

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    private val standardTestDispatcher = StandardTestDispatcher()


    private var spaceXApi: SpaceXApi = mock()
    private val resourceProvider: ResourceProvider = mock()

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        Dispatchers.setMain(standardTestDispatcher)

        val noNetworkMessage = "CONNECTION"
        val generalErrorMessage = "OOPS"

//        whenever(
//            resourceProvider.getResource(R.string.no_internet_connection)
//        ).thenReturn(noNetworkMessage)
//        whenever(
//            resourceProvider.getResource(R.string.something_went_wrong)
//        ).thenReturn(generalErrorMessage)

        spaceXViewModel = SpaceXViewModel(
            resourceProvider = resourceProvider,
            spaceXApi = spaceXApi
        )
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        standardTestDispatcher.cancelChildren()
    }

    @Test
    fun getState() {
    }

    @Test
    fun dispatch() {

        runBlocking {
            spaceXViewModel.dispatch(Action.LaunchItemLinkClicked(LaunchItemLink(
                title = "Google",
                url = "www.google.com"
            )))
            spaceXViewModel.state.collect {
                val test = it.events.size
            }

        }
    }

    @Test
    fun removeConsumedEvent() {
    }
}