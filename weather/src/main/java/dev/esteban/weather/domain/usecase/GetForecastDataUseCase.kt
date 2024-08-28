package dev.esteban.weather.domain.usecase

import dev.esteban.weather.data.repository.WeatherRepository
import dev.esteban.weather.domain.mapper.WeatherForecastMapper
import dev.esteban.weather.domain.model.ForecastWeather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetForecastDataUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val weatherForecastMapper: WeatherForecastMapper
) {
    operator fun invoke(
        latitude: Double?,
        longitude: Double?
    ): Flow<List<ForecastWeather>> = flow {
        val forecastWeather = weatherRepository.getForecastWeather(latitude, longitude)
        val forecastWeatherList = weatherForecastMapper.getForecastWeatherList(forecastWeather.list)
        emit(forecastWeatherList.take(5))
    }
}
