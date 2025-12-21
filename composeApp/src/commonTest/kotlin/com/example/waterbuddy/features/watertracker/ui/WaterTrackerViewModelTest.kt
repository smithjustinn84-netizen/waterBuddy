package com.example.waterbuddy.features.watertracker.ui

import app.cash.turbine.test
import com.example.waterbuddy.core.navigation.NavigationCommand
import com.example.waterbuddy.core.navigation.Navigator
import com.example.waterbuddy.core.navigation.Route
import com.example.waterbuddy.features.watertracker.domain.model.DailyWaterStats
import com.example.waterbuddy.features.watertracker.domain.repository.WaterRepository
import com.example.waterbuddy.features.watertracker.domain.usecase.AddWaterIntakeUseCase
import com.example.waterbuddy.features.watertracker.domain.usecase.DeleteWaterIntakeUseCase
import com.example.waterbuddy.features.watertracker.domain.usecase.ObserveDailyWaterStatsUseCase
import com.example.waterbuddy.features.watertracker.domain.usecase.UpdateDailyGoalUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class WaterTrackerViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testDate = LocalDate(2023, 10, 27)

    private lateinit var viewModel: WaterTrackerViewModel
    private val statsFlow = MutableSharedFlow<DailyWaterStats>(replay = 1)

    private val fakeRepository = object : WaterRepository {
        override fun observeDailyStats(date: LocalDate): Flow<DailyWaterStats> = statsFlow
        override fun observeStatsRange(
            startDate: LocalDate,
            endDate: LocalDate
        ): Flow<List<DailyWaterStats>> = flowOf(emptyList())

        override suspend fun addWaterIntake(amountMl: Int, note: String?): Result<Unit> =
            Result.success(Unit)

        override suspend fun deleteWaterIntake(id: String): Result<Unit> = Result.success(Unit)
        override suspend fun updateDailyGoal(goalMl: Int): Result<Unit> = Result.success(Unit)
        override suspend fun getDailyGoal(): Int = 2000
    }

    private val fakeNavigator = object : Navigator {
        override val commands = MutableSharedFlow<NavigationCommand>()
        override fun navigate(destination: Route, clearBackStack: Boolean) {}
        override fun goBack() {}
    }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        val observeUseCase = ObserveDailyWaterStatsUseCase(fakeRepository)
        val addUseCase = AddWaterIntakeUseCase(fakeRepository)
        val deleteUseCase = DeleteWaterIntakeUseCase(fakeRepository)
        val updateUseCase = UpdateDailyGoalUseCase(fakeRepository)

        viewModel = WaterTrackerViewModel(
            observeUseCase,
            addUseCase,
            deleteUseCase,
            updateUseCase,
            fakeNavigator
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        viewModel.state.test {
            val initialState = awaitItem()
            assertEquals(true, initialState.isLoading)
        }
    }

    @Test
    fun `state updates when stats are observed`() = runTest {
        viewModel.state.test {
            // Skip initial state
            assertEquals(true, awaitItem().isLoading)

            val stats = DailyWaterStats(
                date = testDate,
                totalMl = 500,
                goalMl = 2000,
                entries = emptyList()
            )

            statsFlow.emit(stats)

            val updatedState = awaitItem()
            assertEquals(500, updatedState.totalMl)
            assertEquals(2000, updatedState.goalMl)
            assertEquals(false, updatedState.isLoading)
        }
    }

    @Test
    fun `goal reached event is emitted`() = runTest {
        viewModel.events.test {
            // First emit non-reached stats
            statsFlow.emit(
                DailyWaterStats(
                    date = testDate, totalMl = 500, goalMl = 2000, entries = emptyList()
                )
            )

            // Then emit reached stats
            statsFlow.emit(
                DailyWaterStats(
                    date = testDate, totalMl = 2000, goalMl = 2000, entries = emptyList()
                )
            )

            // Advance time for debounce in ViewModel
            advanceTimeBy(1000)

            assertEquals(WaterTrackerUiEvent.GoalReached, awaitItem())
        }
    }
}
