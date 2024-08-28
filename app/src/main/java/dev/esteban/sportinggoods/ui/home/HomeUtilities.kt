package dev.esteban.sportinggoods.ui.home

import dev.esteban.common.utils.ScreenState
import dev.esteban.weather.domain.model.CurrentWeather

data class HomeUiState(
    val screenState: ScreenState = ScreenState.None,
    val error: HomeError? = null,
    val currentWeather: CurrentWeather? = null,
    val isRefreshing: Boolean = false,
)

enum class HomeError { NO_LOCATION_PROVIDED, NO_LOCATION_PROVIDED_DO_NOT_ASK_AGAIN, GENERIC }
