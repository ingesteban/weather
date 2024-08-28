package dev.esteban.weather.domain.mapper

import dev.esteban.common.network.convertLongToDay
import dev.esteban.weather.data.datasource.remote.model.NetworkForecastWeatherInfoModel
import dev.esteban.weather.data.datasource.remote.model.NetworkWeatherDetailModel
import dev.esteban.weather.domain.model.ForecastWeather

class WeatherForecastMapper {

    fun getForecastWeatherList(networkForecastWeatherInfoModelList: List<NetworkForecastWeatherInfoModel>): List<ForecastWeather> {
        val networkForecastWeatherList = networkForecastWeatherInfoModelList
            .groupBy { it.dt.convertLongToDay() }

        val forecastWeatherList = networkForecastWeatherList.map {
            val date = it.key
            val forecastWeatherList = it.value
            ForecastWeather(
                highTemp = forecastWeatherList.getHighTemp(),
                lowTemp = forecastWeatherList.getLowTemp(),
                date = date,
                windSpeed = forecastWeatherList.windSpeed(),
                windDirection = forecastWeatherList.windDirection(),
                windDescription = forecastWeatherList.getMostCommonValue { it.description },
                icon = forecastWeatherList.getMostCommonValue { it.icon }
            )
        }

        return forecastWeatherList
    }

    fun List<NetworkForecastWeatherInfoModel>.windSpeed() = this.sumOf { it.wind.speed } / this.size
    fun List<NetworkForecastWeatherInfoModel>.windDirection() =
        this.sumOf { it.wind.deg } / this.size

    fun List<NetworkForecastWeatherInfoModel>.getHighTemp() =
        this.maxBy { it.main.tempMax }.main.tempMax

    fun List<NetworkForecastWeatherInfoModel>.getLowTemp() =
        this.minBy { it.main.tempMax }.main.tempMin

    fun <T> List<NetworkForecastWeatherInfoModel>.getMostCommonValue(selector: (NetworkWeatherDetailModel) -> T): T {
        val weather = this.flatMap { it.weather }
        return weather.groupingBy(selector).eachCount().maxBy { it.value }.key
    }
}