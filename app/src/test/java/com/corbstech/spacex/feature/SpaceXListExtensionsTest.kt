package com.corbstech.spacex.feature

import com.corbstech.spacex.feature.filtermenu.FilterMenuItem
import com.corbstech.spacex.feature.list.checkSuccessOutcome
import com.corbstech.spacex.feature.list.checkYear
import com.corbstech.spacex.feature.list.formatLongToLocalCurrency
import com.corbstech.spacex.feature.list.getDaysBetweenNowAndZonedDateTime
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.threeten.bp.ZonedDateTime

class SpaceXListExtensionsTest {

    @Test
    fun `checkYear should return true ONLY if year is contained in the selected years list`() {

        val selectedYears = listOf(2022, 2021, 2020)

        assertTrue(selectedYears.checkYear(2021))

        assertFalse(selectedYears.checkYear(2012))
    }

    @Test
    fun `Format Long to local currency number format - confirm success`() {

        val testLong = 74000000000L
        val expectedString = "74,000,000,000"

        assertThat(testLong.formatLongToLocalCurrency(), equalTo(expectedString))
    }

    @Test
    fun `Format Long to local currency number format - confirm failure`() {

        val testLong = 74000000000L
        val expectedString = "74000000000"

        assertThat(testLong.formatLongToLocalCurrency(), not(equalTo(expectedString)))
    }

    @Test
    fun `Get days between value and text from future ZonedDateTime`() {

        val test100DaysFromNow = ZonedDateTime.now().plusDays(100)
        val expectedDaysUntil100 = 99L

        assertThat(
            test100DaysFromNow.getDaysBetweenNowAndZonedDateTime(),
            equalTo(expectedDaysUntil100)
        )
    }

    @Test
    fun `Get days between value and text from past ZonedDateTime`() {

        val test100DaysSinceNow = ZonedDateTime.now().minusDays(100)
        val expectedDaysSince100 = -100L

        assertThat(
            test100DaysSinceNow.getDaysBetweenNowAndZonedDateTime(),
            equalTo(expectedDaysSince100)
        )
    }

    @Test
    fun `verify checkSuccessOutcome for the various combinations`() {

        val successOutcomesEmpty = listOf<FilterMenuItem.SuccessOutcome>()

        assertTrue(successOutcomesEmpty.checkSuccessOutcome(true))
        assertTrue(successOutcomesEmpty.checkSuccessOutcome(false))
        assertTrue(successOutcomesEmpty.checkSuccessOutcome(null))

        val successOutcomeSucceededSelected = listOf(
            FilterMenuItem.SuccessOutcome.Succeeded
        )

        assertTrue(successOutcomeSucceededSelected.checkSuccessOutcome(true))
        assertFalse(successOutcomeSucceededSelected.checkSuccessOutcome(false))
        assertFalse(successOutcomeSucceededSelected.checkSuccessOutcome(null))

        val successOutcomeFailedSelected = listOf(
            FilterMenuItem.SuccessOutcome.Failed
        )

        assertFalse(successOutcomeFailedSelected.checkSuccessOutcome(true))
        assertTrue(successOutcomeFailedSelected.checkSuccessOutcome(false))
        assertFalse(successOutcomeFailedSelected.checkSuccessOutcome(null))

        val successOutcomePendingSelected = listOf(
            FilterMenuItem.SuccessOutcome.Pending
        )

        assertFalse(successOutcomePendingSelected.checkSuccessOutcome(true))
        assertFalse(successOutcomePendingSelected.checkSuccessOutcome(false))
        assertTrue(successOutcomePendingSelected.checkSuccessOutcome(null))

        val twoSuccessOutcomesSelected = listOf(
            FilterMenuItem.SuccessOutcome.Succeeded,
            FilterMenuItem.SuccessOutcome.Pending
        )

        assertTrue(twoSuccessOutcomesSelected.checkSuccessOutcome(true))
        assertFalse(twoSuccessOutcomesSelected.checkSuccessOutcome(false))
        assertTrue(twoSuccessOutcomesSelected.checkSuccessOutcome(null))

        val allSuccessOutcomesSelected = listOf(
            FilterMenuItem.SuccessOutcome.Succeeded,
            FilterMenuItem.SuccessOutcome.Failed,
            FilterMenuItem.SuccessOutcome.Pending
        )

        assertTrue(allSuccessOutcomesSelected.checkSuccessOutcome(true))
        assertTrue(allSuccessOutcomesSelected.checkSuccessOutcome(false))
        assertTrue(allSuccessOutcomesSelected.checkSuccessOutcome(null))
    }
}