package com.corbstech.spacex.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corbstech.spacex.R
import com.corbstech.spacex.app.api.SpaceXApi
import com.corbstech.spacex.app.api.model.*
import com.corbstech.spacex.app.framework.ResourceProvider
import com.corbstech.spacex.app.ui.list.RecyclerItem
import com.corbstech.spacex.feature.filtermenu.FilterMenuState
import com.corbstech.spacex.feature.filtermenu.FilterMenuItem
import com.corbstech.spacex.feature.filtermenu.SortOrder
import com.corbstech.spacex.feature.list.*
import com.corbstech.spacex.feature.list.company.CompanyItem
import com.corbstech.spacex.feature.list.header.HeaderItem
import com.corbstech.spacex.feature.list.launch.LaunchItem
import com.corbstech.spacex.feature.list.launch.LaunchItemLink
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import javax.inject.Inject

@HiltViewModel
open class SpaceXViewModel @Inject constructor(
    private val spaceXApi: SpaceXApi,
    private val resourceProvider: ResourceProvider,
    private var ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    // region Members & init

    private val actionDispatcher = MutableSharedFlow<Action>()
    private val mutableState = MutableStateFlow(ViewSate())
    private val launchItems = mutableListOf<LaunchItem>()
    val state: StateFlow<ViewSate>
        get() = mutableState

    fun init() {
        viewModelScope.launch {
            handleActions()
        }
        viewModelScope.launch {
            syncSpaceXData()
        }
    }

    // endregion

    // region Network data source

    private suspend fun syncSpaceXData() {
        withContext(ioDispatcher) {
            mutableState.update { current ->
                current.copy(refreshing = true)
            }
            var companyData: Company? = null
            var launchData: LaunchData? = null
            coroutineScope {
                try {
                    spaceXApi.getCompany().body()?.let {
                        companyData = it
                    }
                    spaceXApi.getLaunchData(LaunchDataRequestBody(Options(), Query)).body()?.let {
                        launchData = it
                    }
                } catch (e: Throwable) {
                    mutableState.update { current ->
                        current.copy(refreshing = false, noData = true)
                    }
                }
            }
            loadViewState(companyData, launchData)
        }
    }

    // endregion

    // region View state

    private fun loadViewState(companyData: Company?, launchData: LaunchData?) {
        companyData?.let { company ->
            launchData?.launches?.let { launches ->
                launchItems.addAll(buildLaunchItemsList(launches))
                mutableState.update { current ->
                    current.copy(
                        filterMenuState = buildFilterMenu(launchItems),
                        staticItems = buildStaticItemsList(company = company),
                        launchItems = launchItems,
                        refreshing = false,
                        noData = false
                    )
                }
            }
        }
    }

    // endregion

    // region Static list items

    private fun buildStaticItemsList(
        company: Company
    ): List<RecyclerItem> {
        return listOf(
            HeaderItem(
                id = -1L,
                title = resourceProvider.getResource(R.string.company)
            ),
            CompanyItem(
                id = -2L,
                info = resourceProvider.getResource(
                    R.string.company_info,
                    company.name,
                    company.founder,
                    company.founded,
                    company.employees,
                    company.launchSites,
                    company.valuation.formatLongToLocalCurrency(),
                )
            ),
            HeaderItem(
                id = -3L,
                title = resourceProvider.getResource(R.string.launches)
            )
        )
    }

    // endregion

    // region Launch items

    private fun buildLaunchItemsList(launches: List<Launch>) = launches.map { launch ->
        val zonedDateTime = Instant.parse(launch.dateUtc).atZone(ZoneId.systemDefault())
        LaunchItem(
            id = launch.flightNumber?.toLong() ?: 0L,
            mission = launch.name,
            date = zonedDateTime.getDisplayDateTimeFromZonedDateTime(),
            year = zonedDateTime.getYearFromZonedDateTime(),
            rocket = resourceProvider.getResource(
                R.string.rocket_info,
                launch.rocket?.name,
                launch.rocket?.type
            ),
            daysLabelAndValue = zonedDateTime.getDaysBetweenNowAndZonedDateTime(),
            patchImage = launch.links?.patch?.small?.replace("https", "http"), // TODO Remove
            success = launch.success,
            successImage = when (launch.success) {
                true -> R.drawable.ic_check
                false -> R.drawable.ic_cross
                else -> null
            },
            links = buildLaunchItemLinks(launch.links)
        )
    }

    // endregion

    // region Filter & sort

    private fun updateLaunchItems() {
        mutableState.update { current ->
            val successSelections = mutableListOf<FilterMenuItem.SuccessOutcome>()
            val yearSelections = mutableListOf<Int>()
            var order: SortOrder = SortOrder.None
            current.filterMenuState.filterOptionMap.values.flatten().forEach {
                when (it.filterMenuType) {
                    is FilterMenuItem.FilterMenuType.Filter -> {
                        when (it.filterMenuType.filterType) {
                            is FilterMenuItem.FilterType.Year -> {
                                it.filterMenuType.filterType.year?.let { year ->
                                    if (it.selected) yearSelections.add(year)
                                }
                            }
                            is FilterMenuItem.FilterType.Success -> {
                                if (it.selected) {
                                    successSelections.add(
                                        it.filterMenuType.filterType.successOutcome
                                    )
                                }
                            }
                        }
                    }
                    is FilterMenuItem.FilterMenuType.Sort -> {
                        if (it.selected) {
                            order = it.filterMenuType.sortOrder
                        }
                    }
                }
            }
            current.copy(
                launchItems = launchItems.filter {
                    yearSelections.checkYear(it.year) &&
                            successSelections.checkSuccessOutcome(successValue = it.success)
                }.sortLaunchItems(order)
            )
        }
    }

    // endregion

    // region Launch item links

    private fun buildLaunchItemLinks(links: Links?) = listOf(
        LaunchItemLink(
            url = links?.articleLink, title = resourceProvider.getResource(R.string.article)
        ),
        LaunchItemLink(
            url = links?.webcast, title = resourceProvider.getResource(R.string.video)
        ),
        LaunchItemLink(
            url = links?.wikipedia, title = resourceProvider.getResource(R.string.wikipedia)
        )
    ).filterNot { it.url.isNullOrBlank() }

    private fun onLaunchItemLinkClicked(uniqueId: String, launchItemLink: LaunchItemLink) =
        launchItemLink.url?.let { url ->
            mutableState.update { current ->
                current.copy(
                    events = current.events + Event.LaunchWebBrowser(url = url, uniqueId = uniqueId)
                )
            }
        }

    // endregion

    // region Filter menu

    private fun buildFilterMenu(launchItems: List<LaunchItem>): FilterMenuState {
        val headerList = listOf(
            FilterMenuItem(
                itemName = resourceProvider.getResource(R.string.sort),
                filterMenuType = FilterMenuItem.FilterMenuType.Sort()
            ),
            FilterMenuItem(
                itemName = resourceProvider.getResource(R.string.launch_year),
                filterMenuType = FilterMenuItem.FilterMenuType.Filter(
                    FilterMenuItem.FilterType.Year()
                )
            ),
            FilterMenuItem(
                itemName = resourceProvider.getResource(R.string.launch_success),
                filterMenuType = FilterMenuItem.FilterMenuType.Filter(
                    FilterMenuItem.FilterType.Success(FilterMenuItem.SuccessOutcome.All)
                )
            ),
        )
        return FilterMenuState(
            headerList = headerList,
            filterOptionMap = hashMapOf(
                headerList[0] to listOf(
                    FilterMenuItem(
                        itemName = resourceProvider.getResource(R.string.asc),
                        filterMenuType = FilterMenuItem.FilterMenuType.Sort(SortOrder.Asc)
                    ),
                    FilterMenuItem(
                        itemName = resourceProvider.getResource(R.string.desc),
                        filterMenuType = FilterMenuItem.FilterMenuType.Sort(SortOrder.Desc)
                    )
                ),
                headerList[1] to launchItems.distinctBy { it.year }
                    .mapNotNull { it.year }
                    .sortedByDescending { it }
                    .map {
                        FilterMenuItem(
                            itemName = it.toString(),
                            filterMenuType =
                            FilterMenuItem.FilterMenuType.Filter(
                                FilterMenuItem.FilterType.Year(it)
                            )
                        )
                    },
                headerList[2] to listOf(
                    FilterMenuItem(
                        itemName = resourceProvider.getResource(R.string.succeeded),
                        filterMenuType = FilterMenuItem.FilterMenuType.Filter(
                            FilterMenuItem.FilterType.Success(
                                FilterMenuItem.SuccessOutcome.Succeeded
                            )
                        )
                    ),
                    FilterMenuItem(
                        itemName = resourceProvider.getResource(R.string.failed),
                        filterMenuType = FilterMenuItem.FilterMenuType.Filter(
                            FilterMenuItem.FilterType.Success(
                                FilterMenuItem.SuccessOutcome.Failed
                            )
                        )
                    ),
                    FilterMenuItem(
                        itemName = resourceProvider.getResource(R.string.pending),
                        filterMenuType = FilterMenuItem.FilterMenuType.Filter(
                            FilterMenuItem.FilterType.Success(
                                FilterMenuItem.SuccessOutcome.Pending
                            )
                        )
                    )
                )
            )
        )
    }

    private fun onFilterMenuItemClicked(
        filterMenuGroup: FilterMenuItem,
        filterMenuItem: FilterMenuItem
    ) {
        updateFilterMenu(filterMenuGroup, filterMenuItem)
        updateLaunchItems()
    }

    private fun updateFilterMenu(
        filterMenuGroup: FilterMenuItem, filterMenuItem: FilterMenuItem
    ) {
        mutableState.update { current ->
            val updateMap = current.filterMenuState.filterOptionMap.toMutableMap()
            val updateList = updateMap[filterMenuGroup]?.toMutableList() ?: mutableListOf()
            if (filterMenuItem.filterMenuType is FilterMenuItem.FilterMenuType.Sort) {
                updateList.forEachIndexed { index, item ->
                    updateList[index] = item.copy(selected = false)
                }
            }
            updateList[updateList.indexOfFirst {
                it.uniqueId == filterMenuItem.uniqueId
            }] = filterMenuItem.copy(selected = filterMenuItem.selected.not())
            updateMap[filterMenuGroup] = updateList
            current.copy(
                filterMenuState = current.filterMenuState.copy(
                    filterOptionMap = updateMap,
                    filtersApplied = updateMap.values.flatten().any {
                        it.selected && it.filterMenuType !is FilterMenuItem.FilterMenuType.Sort
                    }
                )
            )
        }
    }

    // endregion

    // region Actions

    fun dispatch(action: Action) = viewModelScope.launch {
        actionDispatcher.emit(action)
    }

    private suspend fun handleActions(): Nothing = actionDispatcher.collect { action ->
        when (action) {
            is Action.LaunchItemLinkClicked -> {
                onLaunchItemLinkClicked(action.uniqueId, action.launchItemLink)
            }
            is Action.FilterMenuItemClicked -> {
                onFilterMenuItemClicked(action.filterMenuGroup, action.filterMenuItem)
            }
        }
    }

    // endregion

    // region Events

    fun removeConsumedEvent(eventId: String) = mutableState.update { current ->
        current.copy(events = current.events.filterNot { it.uniqueId == eventId })
    }

    // endregion
}