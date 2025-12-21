package com.example.waterbuddy.features.insights.ui

import app.cash.turbine.test
import com.example.waterbuddy.features.insights.domain.usecase.GetHydrationInsightsUseCase
import com.example.waterbuddy.features.watertracker.domain.model.DailyWaterStats
import com.example.waterbuddy.features.watertracker.domain.repository.WaterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class HydrationInsightsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val statsFlow = MutableSharedFlow<List<DailyWaterStats>>(replay = 0)

    private val fakeRepository = object : WaterRepository {
        override fun observeDailyStats(date: LocalDate): Flow<DailyWaterStats> = error("Not used")
        override fun observeStatsRange(
            startDate: LocalDate,
            endDate: LocalDate
        ): Flow<List<DailyWaterStats>> = statsFlow

        override suspend fun addWaterIntake(amountMl: Int, note: String?): Result<Unit> =
            Result.success(Unit)

        override suspend fun deleteWaterIntake(id: String): Result<Unit> = Result.success(Unit)
        override suspend fun updateDailyGoal(goalMl: Int): Result<Unit> = Result.success(Unit)
        override suspend fun getDailyGoal(): Int = 2000
    }

    private lateinit var viewModel: HydrationInsightsViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val getHydrationInsightsUseCase = GetHydrationInsightsUseCase(fakeRepository)
        viewModel = HydrationInsightsViewModel(getHydrationInsightsUseCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(true, state.isLoading)
        }
    }

    @Test
    fun `state updates when insights are loaded`() = runTest {
        viewModel.state.test {
            // Initial loading state
            assertEquals(true, awaitItem().isLoading)

            val stats = listOf(
                DailyWaterStats(LocalDate(2023, 10, 27), 2000, 2000, emptyList())
            )
            statsFlow.emit(stats)

            val state = awaitItem()
            assertEquals(false, state.isLoading)
            assertNotNull(state.insights)
            assertEquals(2000, state.insights?.averageIntake)
        }
    }

    @Test
    fun `error handling when use case fails with flow exception`() = runTest {
        val trigger = MutableSharedFlow<Unit>()
        val failingRepository = object : WaterRepository {
            override fun observeDailyStats(date: LocalDate): Flow<DailyWaterStats> =
                error("Not used")

            override fun observeStatsRange(
                startDate: LocalDate,
                endDate: LocalDate
            ): Flow<List<DailyWaterStats>> = flow {
                trigger.first()
                throw Exception("Test error")
            }

            override suspend fun addWaterIntake(amountMl: Int, note: String?): Result<Unit> =
                Result.success(Unit)

            override suspend fun deleteWaterIntake(id: String): Result<Unit> = Result.success(Unit)
            override suspend fun updateDailyGoal(goalMl: Int): Result<Unit> = Result.success(Unit)
            override suspend fun getDailyGoal(): Int = 2000
        }

        val viewModel = HydrationInsightsViewModel(GetHydrationInsightsUseCase(failingRepository))

        viewModel.state.test {
            // Loading
            assertEquals(true, awaitItem().isLoading)

            trigger.emit(Unit)

            // Error
            val state = awaitItem()
            assertEquals(false, state.isLoading)
            assertEquals("Test error", state.errorMessage)
        }
    }

    @Test
    fun `error handling when use case throws synchronously`() = runTest {
        val failingRepository = object : WaterRepository {
            override fun observeDailyStats(date: LocalDate): Flow<DailyWaterStats> =
                error("Not used")

            override fun observeStatsRange(
                startDate: LocalDate,
                endDate: LocalDate
            ): Flow<List<DailyWaterStats>> {
                throw RuntimeException("Synchronous error")
            }

            override suspend fun addWaterIntake(amountMl: Int, note: String?): Result<Unit> =
                Result.success(Unit)

            override suspend fun deleteWaterIntake(id: String): Result<Unit> = Result.success(Unit)
            override suspend fun updateDailyGoal(goalMl: Int): Result<Unit> = Result.success(Unit)
            override suspend fun getDailyGoal(): Int = 2000
        }

        val viewModel = HydrationInsightsViewModel(GetHydrationInsightsUseCase(failingRepository))

        viewModel.state.test {
            // It might fail immediately during init, so we expect error state
            val state = awaitItem()
            // Depending on how fast init runs vs test collector, we might see loading then error or just error
            if (state.isLoading) {
                val errorState = awaitItem()
                assertEquals(false, errorState.isLoading)
                assertEquals("Synchronous error", errorState.errorMessage)
            } else {
                assertEquals("Synchronous error", state.errorMessage)
            }
        }
    }

    @Test
    fun `ShowError event is emitted on failure`() = runTest {
        val failingRepository = object : WaterRepository {
            override fun observeDailyStats(date: LocalDate): Flow<DailyWaterStats> =
                error("Not used")

            override fun observeStatsRange(
                startDate: LocalDate,
                endDate: LocalDate
            ): Flow<List<DailyWaterStats>> = flow {
                throw Exception("Test error")
            }

            override suspend fun addWaterIntake(amountMl: Int, note: String?): Result<Unit> =
                Result.success(Unit)

            override suspend fun deleteWaterIntake(id: String): Result<Unit> = Result.success(Unit)
            override suspend fun updateDailyGoal(goalMl: Int): Result<Unit> = Result.success(Unit)
            override suspend fun getDailyGoal(): Int = 2000
        }

        val viewModel = HydrationInsightsViewModel(GetHydrationInsightsUseCase(failingRepository))

        viewModel.events.test {
            assertEquals(HydrationInsightsUiEvent.ShowError("Test error"), awaitItem())
        }
    }

    @Test
    fun `intent SelectTimeRange updates state`() = runTest {
        viewModel.state.test {
            // Initial state
            assertEquals(TimeRange.WEEK, awaitItem().selectedTimeRange)

            viewModel.handleIntent(HydrationInsightsUiIntent.SelectTimeRange(TimeRange.MONTH))

            assertEquals(TimeRange.MONTH, awaitItem().selectedTimeRange)
        }
    }

    @Test
    fun `intent Refresh reloads insights`() = runTest {
        viewModel.state.test {
            // Initial loading
            assertEquals(true, awaitItem().isLoading)

            val stats = listOf(DailyWaterStats(LocalDate(2023, 10, 27), 2000, 2000, emptyList()))
            statsFlow.emit(stats)
            assertEquals(false, awaitItem().isLoading) // stats loaded

            viewModel.handleIntent(HydrationInsightsUiIntent.Refresh)

            // Should transition to loading again
            assertEquals(true, awaitItem().isLoading)

            // Emit stats again to complete the cycle and ensure no leftover emissions
            statsFlow.emit(stats)
            assertEquals(false, awaitItem().isLoading)
        }
    }
}
