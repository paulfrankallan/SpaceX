package com.corbstech.spacex.shared.ui.filterdrawer

import com.corbstech.spacex.shared.Order
import java.util.*

data class FilterMenuItem(
    val uniqueId: String = UUID.randomUUID().toString(),
    val itemName: String = "",
    val selected: Boolean = false,
    val filterMenuEvent: FilterMenuEvent = FilterMenuEvent.None
) {
    sealed class FilterMenuEvent {
        class Sort(val order: Order = Order.None) : FilterMenuEvent()
        class Filter(val filterType: FilterType = FilterType.None) : FilterMenuEvent()
        object None : FilterMenuEvent()
    }

    sealed class FilterType {
        class Year(val year: String) : FilterType()
        class Success(val outcome: SuccessOutcome) : FilterType()
        object None : FilterType()
    }

    sealed class SuccessOutcome {
        object Succeeded: SuccessOutcome()
        object Failed : SuccessOutcome()
        object None : SuccessOutcome()
    }
}