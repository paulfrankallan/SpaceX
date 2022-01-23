package com.corbstech.spacex.shared.ui.filterdrawer

import com.corbstech.spacex.shared.SortOrder
import java.util.*

data class FilterMenuItem(
    val uniqueId: String = UUID.randomUUID().toString(),
    val itemName: String = "",
    val selected: Boolean = false,
    val filterMenuType: FilterMenuType,
) {
    sealed class FilterMenuType {
        class Sort(val sortOrder: SortOrder = SortOrder.None) : FilterMenuType()
        class Filter(val filterType: FilterType) : FilterMenuType()
    }

    sealed class FilterType {
        class Year(val year: Int? = null) : FilterType()
        class Success(val successOutcome: SuccessOutcome) : FilterType()
    }

    sealed class SuccessOutcome {
        object Succeeded: SuccessOutcome()
        object Failed : SuccessOutcome()
        object Pending : SuccessOutcome()
        object All : SuccessOutcome()
    }
}