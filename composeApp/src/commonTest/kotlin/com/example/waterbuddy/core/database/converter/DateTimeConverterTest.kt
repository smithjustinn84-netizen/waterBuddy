package com.example.waterbuddy.core.database.converter

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDateTime

class DateTimeConverterTest :
    ShouldSpec(
        {
            val converter = DateTimeConverter()

            context("fromTimestamp") {
                should("return LocalDateTime when given a valid ISO string") {
                    val timestamp = "2023-10-27T10:15:30"
                    val expected = LocalDateTime(2023, 10, 27, 10, 15, 30)
                    val result = converter.fromTimestamp(timestamp)
                    result shouldBe expected
                }

                should("return null when given null") {
                    converter.fromTimestamp(null) shouldBe null
                }
            }

            context("dateToTimestamp") {
                should("return ISO string when given a valid LocalDateTime") {
                    val date = LocalDateTime(2023, 10, 27, 10, 15, 30)
                    val expected = "2023-10-27T10:15:30"
                    val result = converter.dateToTimestamp(date)
                    result shouldBe expected
                }

                should("return null when given null") {
                    converter.dateToTimestamp(null) shouldBe null
                }
            }
        },
    )
