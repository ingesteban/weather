package dev.esteban.weather.data.repository

import dev.esteban.weather.data.datasource.remote.WeatherNetworkDataSource
import dev.esteban.weather.data.datasource.remote.model.NetworkForecastWeatherModel
import dev.esteban.weather.data.datasource.remote.model.NetworkWeatherDataModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val networkDataSource: WeatherNetworkDataSource
) : WeatherRepository {
    override suspend fun getCurrentWeather(lat: Double?, lon: Double?): NetworkWeatherDataModel =
        networkDataSource.getCurrentWeather(lat, lon)

    override suspend fun getForecastWeather(
        lat: Double?,
        lon: Double?
    ): NetworkForecastWeatherModel = networkDataSource.getForecastWeather(lat, lon)
}
