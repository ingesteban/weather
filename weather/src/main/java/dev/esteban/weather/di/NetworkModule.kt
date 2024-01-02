package dev.esteban.weather.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.esteban.weather.data.datasource.remote.WeatherNetworkDataSource
import dev.esteban.weather.data.datasource.remote.WeatherNetworkDataSourceImpl

@Module
@InstallIn(SingletonComponent::class)
interface NetworkModule {
    @Binds
    fun bindsRetrofitNetwork(weatherNetworkDataSourceImpl: WeatherNetworkDataSourceImpl): WeatherNetworkDataSource
}
