package com.corbstech.spacex.shared

import org.threeten.bp.Duration
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import java.util.*

private const val YEAR_FORMAT = "yyyy"
private const val TIME_FORMAT = "hh:mm a"

fun ZonedDateTime.getDaysBetweenFromZonedDateTime(): Pair<String, String> {
    val value = Duration.between(ZonedDateTime.now(), this).toDays()
    return Pair(
        first = "Days ${if (value < 0) "since" else "from"} now:",
        second = value.toString()
    )
}

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