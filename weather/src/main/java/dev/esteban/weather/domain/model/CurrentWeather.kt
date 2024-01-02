package dev.esteban.weather.domain.model

data class CurrentWeather(
    val cityName: String,
    val tempMin: Double,
    val tempMax: Double,
    val feelsLike: Double,
    val windDeg: Int,
    val windSpeed: Double,
    val weather: List<CurrentWeatherItem>,
)

data class CurrentWeatherItem(
    val unixTime: Long,
    val temp: Double,
    val icon: String,
)
