package dev.esteban.sportinggoods.ui.home

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.esteban.common.network.Dispatcher
import dev.esteban.common.network.SportingGodsDispatchers
import dev.esteban.common.network.getEndOfCurrentDay
import dev.esteban.common.network.getStartOfCurrentDay
import dev.esteban.common.utils.ScreenState
import dev.esteban.sportinggoods.utils.LocationUtility
import dev.esteban.weather.domain.usecase.GetWeatherDataUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @Dispatcher(SportingGodsDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val getWeatherDataUseCase: GetWeatherDataUseCase
) : ViewModel() {
    var uiState by mutableStateOf(HomeUiState())
        private set

    fun onRefresh(context: Context) {
        uiState = uiState.copy(isRefreshing = true)
        getWeatherData(context)
    }

    fun getWeatherData(context: Context) {
        uiState = uiState.copy(screenState = ScreenState.Loading)
        LocationUtility.getLocation(
            context = context,
            onLocationGranted = { location ->
                val calendar = Calendar.getInstance()
                getWeatherDataUseCase(
                    location.latitude,
                    location.longitude,
                    calendar.getStartOfCurrentDay(),
                    calendar.getEndOfCurrentDay(),
                )
                    .map { currentWeather ->
                        uiState = uiState.copy(
                            screenState = ScreenState.Success,
                            currentWeather = currentWeather,
                            isRefreshing = false
                        )
                    }
                    .catch {
                        uiState = uiState.copy(
                            screenState = ScreenState.Error,
                            error = HomeError.GENERIC
                        )
                    }
                    .flowOn(ioDispatcher)
                    .launchIn(viewModelScope)
            },
            onLocationNotGranted = {
                showErrorNoLocationProvided()
            }
        )
    }

    fun showErrorNoLocationProvided() {
        uiState = uiState.copy(
            screenState = ScreenState.Error,
            error = HomeError.NO_LOCATION_PROVIDED,
            isRefreshing = false
        )
    }

    fun showErrorNoLocationProvidedAndDoNotAskAgain() {
        uiState = uiState.copy(
            screenState = ScreenState.Error,
            error = HomeError.NO_LOCATION_PROVIDED_DO_NOT_ASK_AGAIN,
            isRefreshing = false
        )
    }
}
