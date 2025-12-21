package com.example.waterbuddy.features.insights.ui

import app.cash.turbine.test
import com.example.waterbuddy.features.insights.domain.usecase.GetHydrationInsightsUseCase
import com.example.waterbuddy.features.watertracker.domain.model.DailyWaterStats
import com.example.waterbuddy.features.watertracker.domain.repository.WaterRepository
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
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
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class HydrationInsightsViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val statsFlow = MutableSharedFlow<List<DailyWaterStats>>(replay = 1)

    private lateinit var repository: WaterRepository
    private lateinit var viewModel: HydrationInsightsViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock<WaterRepository>()

        every { repository.observeStatsRange(any(), any()) } returns statsFlow

        val getHydrationInsightsUseCase = GetHydrationInsightsUseCase(repository)
        viewModel = HydrationInsightsViewModel(getHydrationInsightsUseCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() =
        runTest {
            viewModel.state.test {
                val state = awaitItem()
                assertEquals(true, state.isLoading)
            }
        }

    @Test
    fun `state updates when insights are loaded`() =
        runTest {
            viewModel.state.test {
                // Initial loading state
                assertEquals(true, awaitItem().isLoading)

                val stats =
                    listOf(
                        DailyWaterStats(LocalDate(2023, 10, 27), 2000, 2000, emptyList()),
                    )
                statsFlow.emit(stats)

                val state = awaitItem()
                assertEquals(false, state.isLoading)
                assertNotNull(state.insights)
                assertEquals(2000, state.insights?.averageIntake)
            }
        }

    @Test
    fun `error handling when use case fails with flow exception`() =
        runTest {
            val errorFlow =
                flow<List<DailyWaterStats>> {
                    throw Exception("Test error")
                }
            every { repository.observeStatsRange(any(), any()) } returns errorFlow

            val viewModel = HydrationInsightsViewModel(GetHydrationInsightsUseCase(repository))

            viewModel.state.test {
                // With UnconfinedTestDispatcher, the flow error transitions the state immediately in init
                val state = awaitItem()
                assertEquals(false, state.isLoading)
                assertEquals("Test error", state.errorMessage)
            }
        }

    @Test
    fun `ShowError event is emitted on failure`() =
        runTest {
            val errorFlow =
                flow<List<DailyWaterStats>> {
                    throw Exception("Test error")
                }
            every { repository.observeStatsRange(any(), any()) } returns errorFlow

            val viewModel = HydrationInsightsViewModel(GetHydrationInsightsUseCase(repository))

            viewModel.events.test {
                assertEquals(HydrationInsightsUiEvent.ShowError("Test error"), awaitItem())
            }
        }

    @Test
    fun `intent SelectTimeRange updates state`() =
        runTest {
            viewModel.state.test {
                // Initial state
                assertEquals(TimeRange.WEEK, awaitItem().selectedTimeRange)

                viewModel.handleIntent(HydrationInsightsUiIntent.SelectTimeRange(TimeRange.MONTH))

                assertEquals(TimeRange.MONTH, awaitItem().selectedTimeRange)
            }
        }

    @Test
    fun `intent Refresh reloads insights and clears error`() =
        runTest {
            // 1. Induce an error
            val errorFlow = flow<List<DailyWaterStats>> { throw Exception("Initial Error") }
            every { repository.observeStatsRange(any(), any()) } returns errorFlow

            val viewModel = HydrationInsightsViewModel(GetHydrationInsightsUseCase(repository))

            viewModel.state.test {
                // Get current state (which is error state)
                val errorState = awaitItem()
                assertEquals("Initial Error", errorState.errorMessage)

                // 2. Setup successful reload
                every { repository.observeStatsRange(any(), any()) } returns statsFlow

                viewModel.handleIntent(HydrationInsightsUiIntent.Refresh)

                // 3. Verify error is cleared and loading is true
                val loadingState = awaitItem()
                assertEquals(true, loadingState.isLoading)
                assertNull(loadingState.errorMessage)

                // 4. Complete loading
                statsFlow.emit(emptyList())
                val finalState = awaitItem()
                assertEquals(false, finalState.isLoading)
            }
        }

    @Test
    fun `error handling when use case throws synchronously`() =
        runTest {
            every {
                repository.observeStatsRange(
                    any(),
                    any(),
                )
            } throws RuntimeException("Synchronous error")

            val viewModel = HydrationInsightsViewModel(GetHydrationInsightsUseCase(repository))

            viewModel.state.test {
                // Synchronous error in init transitions state immediately
                val state = awaitItem()
                assertEquals(false, state.isLoading)
                assertEquals("Synchronous error", state.errorMessage)
            }
        }

    @Test
    fun `ShowError event is emitted on synchronous failure`() =
        runTest {
            every {
                repository.observeStatsRange(
                    any(),
                    any(),
                )
            } throws RuntimeException("Synchronous error")

            val viewModel = HydrationInsightsViewModel(GetHydrationInsightsUseCase(repository))

            viewModel.events.test {
                assertEquals(HydrationInsightsUiEvent.ShowError("Synchronous error"), awaitItem())
            }
        }
}
