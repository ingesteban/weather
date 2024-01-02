package dev.esteban.weather.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.esteban.weather.data.repository.WeatherRepositoryImpl
import dev.esteban.weather.data.repository.WeatherRepository

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    fun bindsWeatherRepository(
        weatherRepository: WeatherRepositoryImpl
    ): WeatherRepository
}
