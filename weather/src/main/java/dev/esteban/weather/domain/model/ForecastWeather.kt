package dev.esteban.weather.domain.model

data class ForecastWeather(
    val highTemp: Double,
    val lowTemp: Double,
    val date: Long,
    val windSpeed: Double,
    val windDirection: Int,
    val windDescription: String,
    val icon: String
)
