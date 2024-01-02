package dev.esteban.weather.data.datasource.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkSysModel(
    val type: Int? = null,
    val id: Int? = null,
    val country: String? = null,
    val sunrise: Int? = null,
    val sunset: Int? = null
)

@Serializable
data class NetworkWeatherDataModel(
    val dt: Int,
    val main: NetworkMainModel,
    val weather: List<NetworkWeatherDetailModel>,
    val clouds: NetworkCloudModel,
    val wind: NetworkWindModel,
    val visibility: Int,
    val sys: NetworkSysModel? = null,
    val coord: NetworkCoordModel,
    val base: String,
    val timezone: Int,
    val id: Int,
    val name: String,
    val cod: Int
)
