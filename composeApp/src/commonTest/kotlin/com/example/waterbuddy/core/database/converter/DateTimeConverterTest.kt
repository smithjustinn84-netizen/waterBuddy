package com.example.waterbuddy.core.database.converter

import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DateTimeConverterTest {
    private val converter = DateTimeConverter()

    @Test
    fun fromTimestamp_withValidString_returnsLocalDateTime() {
        val timestamp = "2023-10-27T10:15:30"
        val expected = LocalDateTime(2023, 10, 27, 10, 15, 30)
        val result = converter.fromTimestamp(timestamp)
        assertEquals(expected, result)
    }

    @Test
    fun fromTimestamp_withNull_returnsNull() {
        assertNull(converter.fromTimestamp(null))
    }

    @Test
    fun dateToTimestamp_withValidDate_returnsString() {
        val date = LocalDateTime(2023, 10, 27, 10, 15, 30)
        val expected = "2023-10-27T10:15:30"
        val result = converter.dateToTimestamp(date)
        assertEquals(expected, result)
    }

    @Test
    fun dateToTimestamp_withNull_returnsNull() {
        assertNull(converter.dateToTimestamp(null))
    }
}
