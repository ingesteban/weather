package dev.esteban.weather.data.datasource.remote

import dev.esteban.common.network.RetrofitBuilder
import dev.esteban.weather.data.datasource.remote.model.NetworkForecastWeatherModel
import dev.esteban.weather.data.datasource.remote.model.NetworkWeatherDataModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherNetworkDataSourceImpl @Inject constructor(retrofitBuilder: RetrofitBuilder) :
    WeatherNetworkDataSource {
    private val networkApi = retrofitBuilder.create<WeatherApi>()

    override suspend fun getCurrentWeather(lat: Double?, lon: Double?): NetworkWeatherDataModel =
        networkApi.getCurrentWeather(lat, lon)

    override suspend fun getForecastWeather(
        lat: Double?,
        lon: Double?
    ): NetworkForecastWeatherModel =
        networkApi.getForecastWeather(lat, lon)
}
