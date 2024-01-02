package dev.esteban.sportinggoods.ui.forecast

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.esteban.common.network.Dispatcher
import dev.esteban.common.network.SportingGodsDispatchers
import dev.esteban.common.utils.ScreenState
import dev.esteban.sportinggoods.ui.home.HomeError
import dev.esteban.sportinggoods.utils.LocationUtility
import dev.esteban.weather.domain.usecase.GetForecastDataUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ForecastViewModel @Inject constructor(
    @Dispatcher(SportingGodsDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val getForecastDataUseCase: GetForecastDataUseCase
) : ViewModel() {
    private val _uiState = MutableLiveData(ForecastUiState())
    val uiState: LiveData<ForecastUiState> = _uiState

    fun onRefresh(context: Context) {
        _uiState.postValue(uiState.value?.copy(isRefreshing = true))
        getForecast(context)
    }

    fun getForecast(context: Context) {
        _uiState.postValue(uiState.value?.copy(screenState = ScreenState.Loading))
        LocationUtility.getLocation(context = context, onLocationGranted = { location ->
            getForecastDataUseCase(
                location.latitude,
                location.longitude,
            ).map { forecastWeatherList ->
                    _uiState.postValue(
                        uiState.value?.copy(
                            screenState = ScreenState.Success,
                            forecastWeatherList = forecastWeatherList,
                            isRefreshing = false
                        )
                    )
                }.catch {
                    _uiState.postValue(
                        uiState.value?.copy(
                            screenState = ScreenState.Error,
                            error = ForecastError.GENERIC,
                            isRefreshing = false
                        )
                    )
                }.flowOn(ioDispatcher).launchIn(viewModelScope)
        }, onLocationNotGranted = {
            showErrorNoLocationProvided()
        })
    }

    fun showErrorNoLocationProvided() {
        _uiState.postValue(
            uiState.value?.copy(
                screenState = ScreenState.Error, error = ForecastError.NO_LOCATION_PROVIDED
            )
        )
    }

    fun showErrorNoLocationProvidedAndDoNotAskAgain() {
        _uiState.postValue(
            uiState.value?.copy(
                screenState = ScreenState.Error,
                error = ForecastError.NO_LOCATION_PROVIDED_DO_NOT_ASK_AGAIN
            )
        )
    }
}
