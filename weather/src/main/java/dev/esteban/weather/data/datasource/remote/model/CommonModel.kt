package dev.esteban.weather.data.datasource.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkCoordModel(
    val lon: Double,
    val lat: Double
)

@Serializable
data class NetworkWindModel(
    val speed: Double,
    val deg: Int,
    val gust: Double? = null
)

@Serializable
data class NetworkCloudModel(
    val all: Int
)

@Serializable
data class NetworkMainModel(
    val temp: Double,
    @SerialName("feels_like")
    val feelsLike: Double,
    @SerialName("temp_min")
    val tempMin: Double,
    @SerialName("temp_max")
    val tempMax: Double,
    val pressure: Int,
    val humidity: Int,
    @SerialName("sea_level")
    val seaLevel: Int? = null,
    @SerialName("grnd_level")
    val grndLevel: Int? = null,
    @SerialName("temp_kf")
    val tempKf: Double? = null
)

@Serializable
data class NetworkWeatherDetailModel(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)
