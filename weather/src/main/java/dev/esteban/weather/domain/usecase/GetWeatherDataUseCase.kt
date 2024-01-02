package dev.esteban.weather.domain.usecase

import dev.esteban.weather.data.datasource.remote.model.NetworkForecastWeatherInfoModel
import dev.esteban.weather.data.repository.WeatherRepository
import dev.esteban.weather.domain.model.CurrentWeather
import dev.esteban.weather.domain.model.CurrentWeatherItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetWeatherDataUseCase @Inject constructor(
    val weatherRepository: WeatherRepository
) {
    operator fun invoke(
        latitude: Double?,
        longitude: Double?,
        startOfCurrentDay: Long,
        endOfCurrentDay: Long,
    ): Flow<CurrentWeather> = flow {
        val forecastWeather = weatherRepository.getForecastWeather(latitude, longitude)
        val currentWeather = weatherRepository.getCurrentWeather(latitude, longitude)
        val currentWeatherData = CurrentWeather(
            tempMin = currentWeather.main.tempMin,
            tempMax = currentWeather.main.tempMax,
            feelsLike = currentWeather.main.feelsLike,
            windDeg = currentWeather.wind.deg,
            windSpeed = currentWeather.wind.speed,
            cityName = currentWeather.name,
            weather = getCurrentNetworkForecastWeatherInfoModelList(
                forecastWeather.list,
                (startOfCurrentDay / 1000),
                (endOfCurrentDay / 1000)
            )
        )
        emit(currentWeatherData)
    }

    private fun getCurrentNetworkForecastWeatherInfoModelList(
        list: List<NetworkForecastWeatherInfoModel>,
        startOfCurrentDay: Long,
        endOfCurrentDay: Long,
    ): List<CurrentWeatherItem> {
        val networkForecastWeatherInfoModelList =
            list.filter { it.dt in startOfCurrentDay..endOfCurrentDay }
        return networkForecastWeatherInfoModelList.map {
            CurrentWeatherItem(
                unixTime = it.dt,
                temp = it.main.temp,
                icon = it.weather.first().icon
            )
        }
    }
}
