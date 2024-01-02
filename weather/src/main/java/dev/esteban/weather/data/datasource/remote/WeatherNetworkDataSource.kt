package dev.esteban.weather.data.datasource.remote

import dev.esteban.weather.data.datasource.remote.model.NetworkForecastWeatherModel
import dev.esteban.weather.data.datasource.remote.model.NetworkWeatherDataModel

interface WeatherNetworkDataSource {
    suspend fun getCurrentWeather(
        lat: Double? = null,
        lon: Double? = null,
    ): NetworkWeatherDataModel

    suspend fun getForecastWeather(
        lat: Double? = null,
        lon: Double? = null,
    ): NetworkForecastWeatherModel
}
