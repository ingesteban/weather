package dev.esteban.weather.domain.usecase

import dev.esteban.common.network.convertLongToDay
import dev.esteban.weather.data.repository.WeatherRepository
import dev.esteban.weather.domain.model.ForecastWeather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetForecastDataUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    operator fun invoke(
        latitude: Double?,
        longitude: Double?
    ): Flow<List<ForecastWeather>> = flow {
        val forecastWeather = weatherRepository.getForecastWeather(latitude, longitude)

        val networkForecastWeatherList = forecastWeather.list
            .groupBy { it.dt.convertLongToDay() }

        val forecastWeatherList = networkForecastWeatherList.map { networkForecastWeather ->
            val weatherList = networkForecastWeather.value.flatMap { it.weather }
            val mostCommonDescription =
                weatherList.groupingBy { it.description }.eachCount().maxBy { it.value }.key
            val mostCommonIcon =
                weatherList.groupingBy { it.icon }.eachCount().maxBy { it.value }.key
            ForecastWeather(
                highTemp = networkForecastWeather.value.maxBy { it.main.tempMax }.main.tempMax,
                lowTemp = networkForecastWeather.value.minBy { it.main.tempMax }.main.tempMin,
                date = networkForecastWeather.key,
                windSpeed = (networkForecastWeather.value.sumOf { it.wind.speed } / networkForecastWeather.value.size),
                windDirection = networkForecastWeather.value.sumOf { it.wind.deg } / networkForecastWeather.value.size,
                windDescription = mostCommonDescription,
                icon = mostCommonIcon
            )
        }

        emit(forecastWeatherList.take(5))
    }
}
