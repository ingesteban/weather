package dev.esteban.weather.data.repository

import dev.esteban.weather.data.datasource.remote.model.NetworkForecastWeatherModel
import dev.esteban.weather.data.datasource.remote.model.NetworkWeatherDataModel

interface WeatherRepository {
    suspend fun getCurrentWeather(
        lat: Double?,
        lon: Double?,
    ): NetworkWeatherDataModel

    suspend fun getForecastWeather(
        lat: Double?,
        lon: Double?,
    ): NetworkForecastWeatherModel
}
