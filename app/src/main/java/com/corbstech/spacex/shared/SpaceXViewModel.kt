package com.corbstech.spacex.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corbstech.spacex.R
import com.corbstech.spacex.feature.home.api.LaunchDataRequestBody
import com.corbstech.spacex.feature.home.api.Options
import com.corbstech.spacex.feature.home.api.Query
import com.corbstech.spacex.feature.home.api.SpaceXApi
import com.corbstech.spacex.feature.home.data.model.Company
import com.corbstech.spacex.feature.home.data.model.Launch
import com.corbstech.spacex.feature.home.data.model.LaunchData
import com.corbstech.spacex.feature.home.list.company.CompanyItem
import com.corbstech.spacex.feature.home.list.header.HeaderItem
import com.corbstech.spacex.feature.home.list.launch.LaunchItem
import com.corbstech.spacex.shared.ui.filterdrawer.FilterMenuData
import com.corbstech.spacex.shared.ui.filterdrawer.FilterMenuItem
import com.corbstech.spacex.shared.ui.list.RecyclerItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
open class SpaceXViewModel @Inject constructor(
    private val spaceXApi: SpaceXApi,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val mutableState = MutableStateFlow(ViewSate())
    val state: StateFlow<ViewSate>
        get() = mutableState
    val actionDispatcher = MutableSharedFlow<Action>()

    init {
        viewModelScope.launch {
            handleActions()
        }
        viewModelScope.launch {
            val company: Company
            val launchData: LaunchData
            coroutineScope {
                company = spaceXApi.getCompany().body() ?: Company()
                launchData = spaceXApi.getLaunchData(
                    LaunchDataRequestBody(Options(), Query)
                ).body() ?: LaunchData()
            }
            val launchItems = buildLaunchItemsList(launchData.launches)
            mutableState.value = ViewSate(
                filterMenuData = buildFilterMenu(launchItems),
                staticItems = buildStaticItemsList(
                    company = company
                ),
                launchItems = launchItems
            )
        }
    }

    // region Static list items

    private fun buildStaticItemsList(
        company: Company
    ): List<RecyclerItem> {
        return listOf(
            HeaderItem(title = resourceProvider.getResource(R.string.company)),
            buildCompanyItem(company),
            HeaderItem(title = resourceProvider.getResource(R.string.launches))
        )
    }

    private fun buildCompanyItem(company: Company): CompanyItem {
        return CompanyItem(
            info = resourceProvider.getResource(
                R.string.company_info,
                company.name,
                company.founder,
                company.founded,
                company.employees,
                company.launchSites,
                NumberFormat.getInstance(Locale.getDefault()).format(company.valuation),
            )
        )
    }

    // endregion

    // region Launch items

    private fun updateLaunchItems(order: Order) {
        mutableState.value = state.value.copy(
            launchItems = sortLaunchItems(state.value.launchItems, order)
        )
    }

    private fun buildLaunchItemsList(launches: List<Launch>): List<LaunchItem> {
        return launches.map {
            val zonedDateTime = Instant.parse(it.dateUtc).atZone(ZoneId.systemDefault())
            LaunchItem(
                mission = it.name,
                date = zonedDateTime.getDisplayDateTimeFromZonedDateTime(),
                year = zonedDateTime.getYearFromZonedDateTime(),
                rocket = it.rocket?.name ?: "/" + it.rocket?.type, // TODO
                daysLabelAndValue = zonedDateTime.getDaysBetweenFromZonedDateTime(),
                patchImage = it.links?.patch?.small?.replace("https", "http"), // TODO
                successImage = if (it.success) {
                    R.drawable.tick
                } else {
                    R.drawable.cross
                }
            )
        }
    }

    private fun sortLaunchItems(
        launchItems: List<LaunchItem>,
        order: Order
    ) = when (order) {
        Order.NONE -> launchItems
        Order.ASC -> launchItems.sortedBy { it.year }
        Order.DESC -> launchItems.sortedByDescending { it.year }
    }

    // endregion

    // region Filter menu

    private fun buildFilterMenu(launchItems: List<LaunchItem>): FilterMenuData {
        return FilterMenuData(
            headerList = listOf(
                FilterMenuItem((resourceProvider.getResource(R.string.sort))),
                FilterMenuItem((resourceProvider.getResource(R.string.launch_year))),
                FilterMenuItem((resourceProvider.getResource(R.string.launch_success))),
            ),
            childList = hashMapOf(
                FilterMenuItem((resourceProvider.getResource(R.string.sort))) to
                        listOf(
                            resourceProvider.getResource(R.string.asc),
                            resourceProvider.getResource(R.string.desc)
                        ),
                FilterMenuItem((resourceProvider.getResource(R.string.launch_year))) to
                        launchItems.distinctBy { it.year }
                            .sortedByDescending { it.year }
                            .mapNotNull { it.year?.toString() }.toMutableList(),
                FilterMenuItem((resourceProvider.getResource(R.string.launch_success))) to
                        listOf(
                            resourceProvider.getResource(R.string.succeeded),
                            resourceProvider.getResource(R.string.failed)
                        )
            )
        )
    }

    // endregion

    // region Actions

    private suspend fun handleActions() {
        actionDispatcher.collect { intent ->
            when (intent) {
                is Action.Sort -> {
                    updateLaunchItems(intent.order)
                }
            }
        }
    }

    // endregion
}