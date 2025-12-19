package com.example.demometro.domain.usecase
import com.example.demometro.domain.model.DailyWaterStats
import com.example.demometro.domain.repository.WaterRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
@Inject
class ObserveDailyWaterStatsUseCase(
    private val waterRepository: WaterRepository
) {
    operator fun invoke(date: LocalDate): Flow<DailyWaterStats> {
        return waterRepository.observeDailyStats(date)
    }
}
