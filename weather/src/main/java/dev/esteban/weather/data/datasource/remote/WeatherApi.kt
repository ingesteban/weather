package dev.esteban.weather.data.datasource.remote

import dev.esteban.common.network.Constants.CURRENT_WEATHER
import dev.esteban.common.network.Constants.FORECAST_WEATHER
import dev.esteban.weather.data.datasource.remote.model.NetworkForecastWeatherModel
import dev.esteban.weather.data.datasource.remote.model.NetworkWeatherDataModel
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    companion object {
        const val UNITS = "imperial"
    }

    @GET(CURRENT_WEATHER)
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double? = null,
        @Query("lon") lon: Double? = null,
        @Query("units") units: String = UNITS,
    ): NetworkWeatherDataModel

    @GET(FORECAST_WEATHER)
    suspend fun getForecastWeather(
        @Query("lat") lat: Double? = null,
        @Query("lon") lon: Double? = null,
        @Query("units") units: String = UNITS,
    ): NetworkForecastWeatherModel
}
