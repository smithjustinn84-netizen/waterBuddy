package com.example.waterbuddy.features.preferences.presentation

import app.cash.turbine.test
import com.example.waterbuddy.features.watertracker.domain.repository.WaterRepository
import com.example.waterbuddy.features.watertracker.domain.usecase.GetDailyGoalUseCase
import com.example.waterbuddy.features.watertracker.domain.usecase.UpdateDailyGoalUseCase
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PreferencesViewModelTest {

    private val repository = mock<WaterRepository>()
    private val getDailyGoalUseCase = GetDailyGoalUseCase(repository)
    private val updateDailyGoalUseCase = UpdateDailyGoalUseCase(repository)
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads daily goal`() = runTest {
        everySuspend { repository.getDailyGoal() } returns 3000

        val viewModel = PreferencesViewModel(getDailyGoalUseCase, updateDailyGoalUseCase)

        viewModel.uiState.test {
            // With UnconfinedTestDispatcher, the initial load happens immediately during init
            awaitItem().apply {
                isLoading shouldBe false
                dailyGoalMl shouldBe 3000
            }
        }
    }

    @Test
    fun `UpdateDailyGoal event updates state on success`() = runTest {
        everySuspend { repository.getDailyGoal() } returns 2000
        everySuspend { repository.updateDailyGoal(2500) } returns Result.success(Unit)

        val viewModel = PreferencesViewModel(getDailyGoalUseCase, updateDailyGoalUseCase)

        viewModel.uiState.test {
            awaitItem().dailyGoalMl shouldBe 2000 // Initial load result
            viewModel.onEvent(PreferencesUiEvent.UpdateDailyGoal(2500))
            awaitItem().dailyGoalMl shouldBe 2500
        }
    }

    @Test
    fun `UpdateDailyGoal event emits error effect on failure`() = runTest {
        everySuspend { repository.getDailyGoal() } returns 2000

        val viewModel = PreferencesViewModel(getDailyGoalUseCase, updateDailyGoalUseCase)

        viewModel.uiEffect.test {
            // Goal 100 is below MIN_GOAL (500), so it fails in UseCase
            viewModel.onEvent(PreferencesUiEvent.UpdateDailyGoal(100))
            awaitItem().shouldBe(PreferencesUiEffect.ShowError("Goal must be between 500 and 5000 ml"))
        }
    }
}
