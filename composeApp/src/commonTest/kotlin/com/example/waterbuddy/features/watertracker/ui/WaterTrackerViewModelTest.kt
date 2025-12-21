package com.example.waterbuddy.features.watertracker.ui

import app.cash.turbine.test
import com.example.waterbuddy.core.navigation.Navigator
import com.example.waterbuddy.features.watertracker.domain.model.DailyWaterStats
import com.example.waterbuddy.features.watertracker.domain.repository.WaterRepository
import com.example.waterbuddy.features.watertracker.domain.usecase.AddWaterIntakeUseCase
import com.example.waterbuddy.features.watertracker.domain.usecase.DeleteWaterIntakeUseCase
import com.example.waterbuddy.features.watertracker.domain.usecase.ObserveDailyWaterStatsUseCase
import com.example.waterbuddy.features.watertracker.domain.usecase.UpdateDailyGoalUseCase
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class WaterTrackerViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testDate = LocalDate(2023, 10, 27)

    private lateinit var viewModel: WaterTrackerViewModel
    private lateinit var repository: WaterRepository
    private lateinit var navigator: Navigator
    
    private val statsFlow = MutableSharedFlow<DailyWaterStats>(replay = 1)

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        repository = mock<WaterRepository>()
        navigator = mock<Navigator>()

        every { repository.observeDailyStats(any()) } returns statsFlow
        every { repository.observeStatsRange(any(), any()) } returns flowOf(emptyList())
        everySuspend { repository.getDailyGoal() } returns 2000

        val observeUseCase = ObserveDailyWaterStatsUseCase(repository)
        val addUseCase = AddWaterIntakeUseCase(repository)
        val deleteUseCase = DeleteWaterIntakeUseCase(repository)
        val updateUseCase = UpdateDailyGoalUseCase(repository)

        viewModel = WaterTrackerViewModel(
            observeUseCase,
            addUseCase,
            deleteUseCase,
            updateUseCase,
            navigator
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
            assertTrue(initialState.isLoading)
        }
    }

    @Test
    fun `state updates when stats are observed`() = runTest {
        viewModel.state.test {
            // Skip initial state
            assertTrue(awaitItem().isLoading)

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
            assertFalse(updatedState.isLoading)
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

    @Test
    fun `goal reached event is only emitted when crossing threshold`() = runTest {
        viewModel.events.test {
            // 1. Emit reached stats
            statsFlow.emit(
                DailyWaterStats(
                    date = testDate, totalMl = 2000, goalMl = 2000, entries = emptyList()
                )
            )

            advanceTimeBy(1000)
            assertEquals(WaterTrackerUiEvent.GoalReached, awaitItem())

            // 2. Emit another reached stats (e.g. added more water)
            statsFlow.emit(
                DailyWaterStats(
                    date = testDate, totalMl = 2250, goalMl = 2000, entries = emptyList()
                )
            )

            advanceTimeBy(1000)

            // Should not emit another GoalReached event
            expectNoEvents()
        }
    }

    @Test
    fun `add water intent calls repository and shows success`() = runTest {
        everySuspend { repository.addWaterIntake(250, any()) } returns Result.success(Unit)

        viewModel.events.test {
            viewModel.handleIntent(WaterTrackerUiIntent.AddWater(250))

            // Should emit success
            val event = awaitItem()
            assertTrue(event is WaterTrackerUiEvent.ShowSuccess)
            assertEquals("Added 250ml", event.message)

            verifySuspend { repository.addWaterIntake(250, null) }
        }
    }

    @Test
    fun `add water intent shows error on failure and resets loading`() = runTest {
        everySuspend {
            repository.addWaterIntake(250, any())
        } returns Result.failure(Exception("DB Error"))

        viewModel.events.test {
            viewModel.handleIntent(WaterTrackerUiIntent.AddWater(250))

            val event = awaitItem()
            assertTrue(event is WaterTrackerUiEvent.ShowError)
            assertEquals("DB Error", event.message)

            // Verify loading is reset
            assertFalse(viewModel.state.value.isLoading)
        }
    }

    @Test
    fun `delete entry intent calls repository and shows success`() = runTest {
        everySuspend { repository.deleteWaterIntake("123") } returns Result.success(Unit)

        viewModel.events.test {
            viewModel.handleIntent(WaterTrackerUiIntent.DeleteEntry("123"))

            val event = awaitItem()
            assertTrue(event is WaterTrackerUiEvent.ShowSuccess)
            assertEquals("Entry deleted", event.message)

            verifySuspend { repository.deleteWaterIntake("123") }
        }
    }

    @Test
    fun `delete entry intent shows error on failure`() = runTest {
        everySuspend { repository.deleteWaterIntake("123") } returns Result.failure(Exception("Delete failed"))

        viewModel.events.test {
            viewModel.handleIntent(WaterTrackerUiIntent.DeleteEntry("123"))

            val event = awaitItem()
            assertTrue(event is WaterTrackerUiEvent.ShowError)
            assertEquals("Delete failed", event.message)
        }
    }

    @Test
    fun `update goal intent calls repository and shows success`() = runTest {
        everySuspend { repository.updateDailyGoal(3000) } returns Result.success(Unit)

        viewModel.events.test {
            viewModel.handleIntent(WaterTrackerUiIntent.UpdateGoal(3000))

            val event = awaitItem()
            assertTrue(event is WaterTrackerUiEvent.ShowSuccess)
            assertEquals("Goal updated to 3000ml", event.message)

            // Should also close dialog
            assertFalse(viewModel.showGoalDialog.value)

            verifySuspend { repository.updateDailyGoal(3000) }
        }
    }

    @Test
    fun `update goal intent shows error on failure`() = runTest {
        everySuspend { repository.updateDailyGoal(3000) } returns Result.failure(Exception("Update failed"))

        viewModel.events.test {
            // Pre-condition: dialog is shown
            viewModel.handleIntent(WaterTrackerUiIntent.ShowGoalDialog)
            assertTrue(viewModel.showGoalDialog.value)

            viewModel.handleIntent(WaterTrackerUiIntent.UpdateGoal(3000))

            val event = awaitItem()
            assertTrue(event is WaterTrackerUiEvent.ShowError)
            assertEquals("Update failed", event.message)

            // Dialog should stay open on failure
            assertTrue(viewModel.showGoalDialog.value)
        }
    }

    @Test
    fun `show and dismiss goal dialog intents update state`() = runTest {
        viewModel.handleIntent(WaterTrackerUiIntent.ShowGoalDialog)
        assertTrue(viewModel.showGoalDialog.value)

        viewModel.handleIntent(WaterTrackerUiIntent.DismissGoalDialog)
        assertFalse(viewModel.showGoalDialog.value)
    }
}
