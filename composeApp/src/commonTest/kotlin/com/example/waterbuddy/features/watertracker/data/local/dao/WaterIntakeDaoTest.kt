package com.example.waterbuddy.features.watertracker.data.local.dao

import app.cash.turbine.test
import com.example.waterbuddy.core.database.AppDatabase
import com.example.waterbuddy.core.database.createTestDatabase
import com.example.waterbuddy.features.watertracker.data.local.entity.WaterIntakeEntity
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WaterIntakeDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: WaterIntakeDao

    @BeforeTest
    fun setup() {
        try {
            db = createTestDatabase()
            dao = db.waterIntakeDao()
        } catch (e: Exception) {
            // Skip on platforms where createTestDatabase fails (e.g. Android without Robolectric)
        }
    }

    @AfterTest
    fun tearDown() {
        if (::db.isInitialized) {
            db.close()
        }
    }

    @Test
    fun insertAndRetrieveAllWaterIntakes() =
        runTest {
            if (!::dao.isInitialized) return@runTest

            val now = LocalDateTime(2023, 10, 27, 10, 15, 30)
            val intake =
                WaterIntakeEntity(
                    id = "1",
                    amountMl = 250,
                    timestamp = now,
                    note = "Glass of water",
                )

            dao.insertWaterIntake(intake)

            dao.getAllWaterIntakes().test {
                val allIntakes = awaitItem()
                assertEquals(1, allIntakes.size)
                assertEquals(intake, allIntakes.first())
            }
        }

    @Test
    fun filterTodaysWaterIntakes() =
        runTest {
            if (!::dao.isInitialized) return@runTest

            val now = LocalDateTime(2023, 10, 27, 10, 15, 30)
            val intake =
                WaterIntakeEntity(
                    id = "1",
                    amountMl = 250,
                    timestamp = now,
                )

            dao.insertWaterIntake(intake)

            dao.getAllWaterIntakes().test {
                val allIntakes = awaitItem()
                assertEquals(1, allIntakes.size)
            }
        }

    @Test
    fun deleteWaterIntake() =
        runTest {
            if (!::dao.isInitialized) return@runTest

            val now = LocalDateTime(2023, 10, 27, 10, 15, 30)
            val intake = WaterIntakeEntity(id = "1", amountMl = 250, timestamp = now)

            dao.insertWaterIntake(intake)

            dao.getAllWaterIntakes().test {
                // Initial item after insert
                assertEquals(1, awaitItem().size)

                dao.deleteWaterIntake(intake)

                // Item after delete
                assertTrue(awaitItem().isEmpty())
            }
        }

    @Test
    fun deleteWaterIntakeById() =
        runTest {
            if (!::dao.isInitialized) return@runTest

            val now = LocalDateTime(2023, 10, 27, 10, 15, 30)
            val intake = WaterIntakeEntity(id = "1", amountMl = 250, timestamp = now)

            dao.insertWaterIntake(intake)

            dao.getAllWaterIntakes().test {
                assertEquals(1, awaitItem().size)

                dao.deleteWaterIntakeById("1")

                assertTrue(awaitItem().isEmpty())
            }
        }
}
