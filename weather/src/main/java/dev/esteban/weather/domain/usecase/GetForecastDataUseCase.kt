package dev.esteban.weather.domain.usecase

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

        val allDescriptions = forecastWeather.list
            .flatMap { it.weather }
            .map {
                Pair(it.description, it.icon)
            }

        val descriptionOccurrences = allDescriptions
            .groupingBy { it }
            .eachCount()

        val mostCommonIconDescription = descriptionOccurrences
            .maxByOrNull { it.value }?.key

        val forecastWeatherList = forecastWeather.list.map {
            ForecastWeather(
                highTemp = it.main.tempMax,
                lowTemp = it.main.tempMin,
                date = it.dt,
                windSpeed = it.wind.speed,
                windDirection = it.wind.deg,
                windDescription = mostCommonIconDescription?.first
                    ?: it.weather.first().description,
                icon = mostCommonIconDescription?.second ?: it.weather.first().icon
            )
        }
        emit(forecastWeatherList)
    }
}
