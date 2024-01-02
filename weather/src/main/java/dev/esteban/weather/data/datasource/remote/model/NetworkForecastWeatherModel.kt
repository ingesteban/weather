package dev.esteban.weather.data.datasource.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkForecastWeatherModel(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<NetworkForecastWeatherInfoModel>,
    val city: NetworkCityModel
)

@Serializable
data class NetworkForecastWeatherInfoModel(
    val dt: Long,
    val main: NetworkMainModel,
    val weather: List<NetworkWeatherDetailModel>,
    val clouds: NetworkCloudModel,
    val wind: NetworkWindModel,
    val visibility: Int,
    val pop: Double,
    val sys: NetworkForecastSysModel,
    @SerialName("dt_txt")
    val dtTxt: String
)

@Serializable
data class NetworkForecastSysModel(
    val pod: String
)

@Serializable
data class NetworkCityModel(
    val id: Long,
    val name: String,
    val coord: NetworkCoordModel,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)
