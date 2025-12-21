package com.example.waterbuddy.core.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class DateTimeFormattersTest {
    @Test
    fun `formatTime returns correct string for morning time`() {
        val dateTime = LocalDateTime(2023, 10, 27, 9, 30)
        val formatted = formatTime(dateTime)
        assertEquals("9:30 AM", formatted)
    }

    @Test
    fun `formatTime returns correct string for afternoon time`() {
        val dateTime = LocalDateTime(2023, 10, 27, 14, 45)
        val formatted = formatTime(dateTime)
        assertEquals("2:45 PM", formatted)
    }

    @Test
    fun `formatTime returns correct string for midnight`() {
        val dateTime = LocalDateTime(2023, 10, 27, 0, 15)
        val formatted = formatTime(dateTime)
        assertEquals("12:15 AM", formatted)
    }

    @Test
    fun `formatTime returns correct string for noon`() {
        val dateTime = LocalDateTime(2023, 10, 27, 12, 0)
        val formatted = formatTime(dateTime)
        assertEquals("12:00 PM", formatted)
    }

    @Test
    fun `formatTime pads minutes with leading zero`() {
        val dateTime = LocalDateTime(2023, 10, 27, 8, 5)
        val formatted = formatTime(dateTime)
        assertEquals("8:05 AM", formatted)
    }

    @Test
    fun `formatDate returns correct string for given date`() {
        val date = LocalDate(2024, 1, 15)
        val formatted = formatDate(date)
        assertEquals("Jan 15", formatted)
    }

    @Test
    fun `formatDate returns correct string for date with single digit day`() {
        val date = LocalDate(2024, 1, 5)
        val formatted = formatDate(date)
        assertEquals("Jan 05", formatted) // Padding.ZERO was used
    }
}
