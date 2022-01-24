package com.corbstech.spacex.feature.list

import com.corbstech.spacex.feature.filtermenu.FilterMenuItem
import com.corbstech.spacex.feature.filtermenu.SortOrder
import com.corbstech.spacex.feature.list.launch.LaunchItem
import org.threeten.bp.Duration
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import java.text.NumberFormat
import java.util.*

private const val YEAR_FORMAT = "yyyy"
private const val TIME_FORMAT = "hh:mm a"

fun ZonedDateTime.getDaysBetweenNowAndZonedDateTime() =
    Duration.between(ZonedDateTime.now(), this).toDays()

fun ZonedDateTime.getYearFromZonedDateTime(): Int? {
    return try {
        Integer.parseInt(
            this.format(
                DateTimeFormatter.ofPattern(YEAR_FORMAT)
                    .withLocale(Locale.getDefault())
            )
        )
    } catch (e: NumberFormatException) {
        null
    }
}

fun ZonedDateTime.getDisplayDateTimeFromZonedDateTime(): String {
    return try {
        val datePart = format(
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                .withLocale(Locale.getDefault())
        )
        val timePart = format(
            DateTimeFormatter.ofPattern(TIME_FORMAT).withLocale(Locale.getDefault())
        )
        "$datePart at $timePart"
    } catch (e: NumberFormatException) {
        ""
    }
}

fun List<LaunchItem>.sortLaunchItems(
    sortOrder: SortOrder
) = when (sortOrder) {
    SortOrder.None -> this
    SortOrder.Asc -> this.sortedBy { it.id }
    SortOrder.Desc -> this.sortedByDescending { it.id }
}

fun List<Int>.checkYear(yearFilter: Int?) = isEmpty() || this.contains(yearFilter)

fun List<FilterMenuItem.SuccessOutcome>.checkSuccessOutcome(successValue: Boolean?): Boolean {
    return this.isEmpty() || when (successValue) {
        true -> {
            this.contains(FilterMenuItem.SuccessOutcome.Succeeded)
        }
        false -> {
            this.contains(FilterMenuItem.SuccessOutcome.Failed)
        }
        null -> {
            this.contains(FilterMenuItem.SuccessOutcome.Pending)
        }
    }
}

fun Long?.formatLongToLocalCurrency() =
    this?.let { NumberFormat.getInstance(Locale.getDefault()).format(this) } ?: ""