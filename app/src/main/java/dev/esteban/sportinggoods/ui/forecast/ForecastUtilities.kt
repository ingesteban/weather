package dev.esteban.sportinggoods.ui.forecast

import dev.esteban.common.utils.ScreenState
import dev.esteban.weather.domain.model.ForecastWeather

data class ForecastUiState(
    val screenState: ScreenState = ScreenState.None,
    val error: ForecastError? = null,
    val forecastWeatherList: List<ForecastWeather>? = null,
    val isRefreshing: Boolean = false,
)

enum class ForecastError { NO_LOCATION_PROVIDED, NO_LOCATION_PROVIDED_DO_NOT_ASK_AGAIN, GENERIC }
