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
import com.corbstech.spacex.feature.home.data.model.Links
import com.corbstech.spacex.feature.home.list.company.CompanyItem
import com.corbstech.spacex.feature.home.list.header.HeaderItem
import com.corbstech.spacex.feature.home.list.launch.LaunchItem
import com.corbstech.spacex.feature.home.list.launch.LaunchItemLink
import com.corbstech.spacex.shared.ui.filterdrawer.FilterMenuData
import com.corbstech.spacex.shared.ui.filterdrawer.FilterMenuItem
import com.corbstech.spacex.shared.ui.list.RecyclerItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
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

    private val actionDispatcher = MutableSharedFlow<Action>()
    private val mutableState = MutableStateFlow(ViewSate())
    private val launchItems = mutableListOf<LaunchItem>()
    val state: StateFlow<ViewSate>
        get() = mutableState

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
            launchItems.addAll(buildLaunchItemsList(launchData.launches))
            mutableState.value = ViewSate(
                filterMenuData = buildFilterMenu(launchItems),
                staticItems = buildStaticItemsList(company = company),
                launchItems = launchItems
            )
        }
    }

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
                    NumberFormat.getInstance(Locale.getDefault()).format(company.valuation),
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

    private fun updateLaunchItems() {
        mutableState.update { current ->
            val successSelections = mutableListOf<FilterMenuItem.SuccessOutcome>()
            val yearSelections = mutableListOf<Int>()
            var order: SortOrder = SortOrder.None
            current.filterMenuData.filterOptionMap.values.flatten().forEach {
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
                                    successSelections.add(it.filterMenuType.filterType.successOutcome)
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
                    yearSelections.checkYear(it.year) && checkSuccessOutcome(
                        successOutcome = it.success,
                        successSelections = successSelections
                    )
                }.sortLaunchItems(order)
            )
        }
    }

    private fun List<Int>.checkYear(yearFilter: Int?) = isEmpty() || contains(yearFilter)

    private fun checkSuccessOutcome(
        successOutcome: Boolean?, successSelections: List<FilterMenuItem.SuccessOutcome>
    ): Boolean {
        return successSelections.isEmpty() || when (successOutcome) {
            true -> {
                successSelections.contains(FilterMenuItem.SuccessOutcome.Succeeded)
            }
            false -> {
                successSelections.contains(FilterMenuItem.SuccessOutcome.Failed)
            }
            null -> {
                successSelections.contains(FilterMenuItem.SuccessOutcome.Pending)
            }
        }
    }

    private fun List<LaunchItem>.sortLaunchItems(
        sortOrder: SortOrder
    ) = when (sortOrder) {
        SortOrder.None -> this
        SortOrder.Asc -> this.sortedBy { it.id }
        SortOrder.Desc -> this.sortedByDescending { it.id }
    }

    private fun buildLaunchItemsList(launches: List<Launch>) = launches.map { launch ->
        val zonedDateTime = Instant.parse(launch.dateUtc).atZone(ZoneId.systemDefault())
        LaunchItem(
            id = launch.flightNumber?.toLong() ?: 0L,
            mission = launch.name,
            date = zonedDateTime.getDisplayDateTimeFromZonedDateTime(),
            year = zonedDateTime.getYearFromZonedDateTime(),
            rocket = launch.rocket?.name ?: "/" + launch.rocket?.type,
            daysLabelAndValue = zonedDateTime.getDaysBetweenFromZonedDateTime(),
            patchImage = launch.links?.patch?.small?.replace("https", "http"), // TODO
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

    private fun onLaunchItemLinkClicked(launchItemLink: LaunchItemLink) =
        launchItemLink.url?.let { url ->
            mutableState.update { current ->
                current.copy(
                    events = current.events + Event.LaunchWebBrowser(url = url)
                )
            }
        }

    // endregion

    // region Filter menu

    private fun buildFilterMenu(launchItems: List<LaunchItem>): FilterMenuData {
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
        return FilterMenuData(
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
                            FilterMenuItem.FilterType.Success(FilterMenuItem.SuccessOutcome.Succeeded)
                        )
                    ),
                    FilterMenuItem(
                        itemName = resourceProvider.getResource(R.string.failed),
                        filterMenuType = FilterMenuItem.FilterMenuType.Filter(
                            FilterMenuItem.FilterType.Success(FilterMenuItem.SuccessOutcome.Failed)
                        )
                    ),
                    FilterMenuItem(
                        itemName = resourceProvider.getResource(R.string.pending),
                        filterMenuType = FilterMenuItem.FilterMenuType.Filter(
                            FilterMenuItem.FilterType.Success(FilterMenuItem.SuccessOutcome.Pending)
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
            val updateMap = current.filterMenuData.filterOptionMap.toMutableMap()
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
                filterMenuData = current.filterMenuData.copy(
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
                onLaunchItemLinkClicked(action.launchItemLink)
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