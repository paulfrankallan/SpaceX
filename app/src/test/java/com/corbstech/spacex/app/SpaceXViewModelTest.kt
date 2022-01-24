package com.corbstech.spacex.app

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.corbstech.spacex.app.api.SpaceXApi
import com.corbstech.spacex.app.api.model.*
import com.corbstech.spacex.app.framework.ResourceProvider
import com.corbstech.spacex.feature.list.company.CompanyItem
import com.corbstech.spacex.feature.list.launch.LaunchItemLink
import com.corbstech.spacex.framework.MainCoroutineRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.hamcrest.core.IsIterableContaining.hasItem
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import retrofit2.Response

@ExperimentalCoroutinesApi
class SpaceXViewModelTest {

    private lateinit var spaceXViewModel: SpaceXViewModel
    private val spaceXApi = mock<SpaceXApi>()
    private val resourceProvider = mock<ResourceProvider>()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        spaceXViewModel = SpaceXViewModel(
            spaceXApi = spaceXApi,
            resourceProvider = resourceProvider,
            ioDispatcher = Dispatchers.Main
        )
    }

    @Test
    fun `Happy path synced Company & Launch data propagated to ViewState`() = runBlocking {

        whenever(resourceProvider.getResource(any())).thenReturn("Test String")
        whenever(resourceProvider.getResource(any(), any())).thenReturn("Test String 2")

        val company = Company(
            name = "SpaceX",
            founder = "Elon Musk",
            founded = 2002,
            employees = 9500,
            launchSites = 3,
            valuation = 74000000000,
        )

        val launch = Launch(
            flightNumber = 1,
            dateUtc = "2006-03-24T22:30:00.000Z",
            success = false,
            links = Links(null, null, null, null),
            name = "FalconSat",
            rocket = Rocket(name = "Falcon 1", type = "rocket")
        )

        val launchData = LaunchData(launches = listOf(launch))

        whenever(spaceXApi.getCompany()).thenReturn(Response.success(company))
        whenever(
            spaceXApi.getLaunchData(
                LaunchDataRequestBody(Options(), Query)
            )
        ).thenReturn(
            Response.success(launchData)
        )

        spaceXViewModel.init()

        assertThat(spaceXViewModel.state.value.staticItems.size, `is`(3))

        assertThat(
            (spaceXViewModel.state.value.staticItems[1] as CompanyItem).info,
            `is`("Test String 2")
        )

        assertThat(spaceXViewModel.state.value.launchItems.size, `is`(1))

        assertThat(spaceXViewModel.state.value.launchItems[0].mission, `is`(launch.name))
        assertThat(spaceXViewModel.state.value.launchItems[0].success, `is`(launch.success))
    }

    @Test
    fun `Dispatching Action LaunchItemLinkClicked should trigger Event LaunchWebBrowser with url`() {

        spaceXViewModel.init()

        val uniqueId = "1"

        val launchItemLink = LaunchItemLink(title = "Google", url = "www.google.com")
        spaceXViewModel.dispatch(Action.LaunchItemLinkClicked(uniqueId, launchItemLink))

        assertThat(
            spaceXViewModel.state.value.events,
            hasItem<Event.LaunchWebBrowser>(
                hasProperty("url", equalTo("www.google.com"))
            )
        )
    }

    @Test
    fun `Removing ConsumedEvent results in the event being removed`() {

        spaceXViewModel.init()

        val uniqueId = "1"

        val launchItemLink = LaunchItemLink(title = "Google", url = "www.google.com")
        spaceXViewModel.dispatch(
            Action.LaunchItemLinkClicked(
                uniqueId = uniqueId,
                launchItemLink = launchItemLink
            )
        )

        assertThat(
            spaceXViewModel.state.value.events,
            hasItem<Event.LaunchWebBrowser>(
                hasProperty("url", equalTo("www.google.com"))
            )
        )

        spaceXViewModel.removeConsumedEvent(uniqueId)

        assertTrue(spaceXViewModel.state.value.events.isEmpty())
    }

    @Test
    fun `Removing ConsumedEvent results in the correct Event being removed`() {

        spaceXViewModel.init()

        val uniqueId1 = "1"
        val uniqueId2 = "2"

        val launchItemLink1 = LaunchItemLink(title = "Google", url = "www.google.com")
        spaceXViewModel.dispatch(
            Action.LaunchItemLinkClicked(
                uniqueId = uniqueId1,
                launchItemLink = launchItemLink1
            )
        )
        val launchItemLink2 = LaunchItemLink(title = "ASOS", url = "www.asos.com")
        spaceXViewModel.dispatch(
            Action.LaunchItemLinkClicked(
                uniqueId = uniqueId2,
                launchItemLink = launchItemLink2
            )
        )

        assertThat(
            spaceXViewModel.state.value.events,
            hasItem<Event.LaunchWebBrowser>(
                hasProperty("url", equalTo("www.google.com"))
            )
        )

        assertThat(
            spaceXViewModel.state.value.events,
            hasItem<Event.LaunchWebBrowser>(
                hasProperty("url", equalTo("www.asos.com"))
            )
        )

        spaceXViewModel.removeConsumedEvent(uniqueId1)

        assertThat(
            spaceXViewModel.state.value.events,
            hasItem<Event.LaunchWebBrowser>(
                hasProperty("url", equalTo("www.asos.com"))
            )
        )

        assertThat(
            spaceXViewModel.state.value.events,
            not(
                hasItem<Event.LaunchWebBrowser>(
                    hasProperty("url", equalTo("www.google.com"))
                )
            )
        )
    }
}