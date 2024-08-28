package dev.esteban.weather.domain.usecase

import dev.esteban.common.network.convertLongToDay
import dev.esteban.weather.data.datasource.remote.model.NetworkCityModel
import dev.esteban.weather.data.datasource.remote.model.NetworkCloudModel
import dev.esteban.weather.data.datasource.remote.model.NetworkCoordModel
import dev.esteban.weather.data.datasource.remote.model.NetworkForecastSysModel
import dev.esteban.weather.data.datasource.remote.model.NetworkForecastWeatherInfoModel
import dev.esteban.weather.data.datasource.remote.model.NetworkForecastWeatherModel
import dev.esteban.weather.data.datasource.remote.model.NetworkMainModel
import dev.esteban.weather.data.datasource.remote.model.NetworkWeatherDetailModel
import dev.esteban.weather.data.datasource.remote.model.NetworkWindModel
import dev.esteban.weather.data.repository.WeatherRepository
import dev.esteban.weather.domain.mapper.WeatherForecastMapper
import dev.esteban.weather.domain.model.ForecastWeather
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetForecastDataUseCaseTest {
    private val mockWeatherRepository: WeatherRepository = mockk()
    private val mockWeatherForecastMapper: WeatherForecastMapper = mockk()
    private lateinit var getForecastDataUseCase: GetForecastDataUseCase

    @Before
    fun setupBase() {
        MockKAnnotations.init(this)
        getForecastDataUseCase = GetForecastDataUseCase(mockWeatherRepository, mockWeatherForecastMapper)
    }

    @After
    fun tearDown() {
        confirmVerified(mockWeatherRepository)
    }

    @Test
    fun `test invoke success`() = runBlocking {
        val mockNetworkForecastWeatherModel = mockk<NetworkForecastWeatherModel>(relaxed = true)
        val expectedForecastWeatherList = listOf(
            ForecastWeather(
                highTemp = 25.0,
                lowTemp = 18.0,
                date = "2023-12-31",
                windSpeed = 5.0,
                windDirection = 45,
                windDescription = "Breeze",
                icon = "01d"
            )
        )
        coEvery {
            mockWeatherRepository.getForecastWeather(any(), any())
        } returns mockNetworkForecastWeatherModel
        every { mockWeatherForecastMapper.getForecastWeatherList(any()) } returns expectedForecastWeatherList

        val result = getForecastDataUseCase.invoke(40.0, -74.0).first()

        coVerify {  mockWeatherRepository.getForecastWeather(any(), any()) }
        verify { mockWeatherForecastMapper.getForecastWeatherList(any()) }

        assertEquals(expectedForecastWeatherList.take(5), result)
    }

    @Test
    fun `test invoke with null latitude and longitude`() = runBlocking {
        val mockNetworkForecastWeatherModel = mockk<NetworkForecastWeatherModel>(relaxed = true)
        val expectedForecastWeatherList = listOf(
            ForecastWeather(
                highTemp = 25.0,
                lowTemp = 18.0,
                date = "2023-12-31",
                windSpeed = 5.0,
                windDirection = 45,
                windDescription = "Breeze",
                icon = "01d"
            )
        )
        coEvery {
            mockWeatherRepository.getForecastWeather(any(), any())
        } returns mockNetworkForecastWeatherModel
        every { mockWeatherForecastMapper.getForecastWeatherList(any()) } returns expectedForecastWeatherList

        val result = getForecastDataUseCase.invoke(null, null).first()

        coVerify {  mockWeatherRepository.getForecastWeather(null, null) }
        verify { mockWeatherForecastMapper.getForecastWeatherList(any()) }

        assertEquals(expectedForecastWeatherList.take(5), result)
    }

    @Test
    fun `test invoke with empty forecast list`() = runBlocking {
        val mockNetworkForecastWeatherModel = mockk<NetworkForecastWeatherModel>(relaxed = true)
        val expectedForecastWeatherList = emptyList<ForecastWeather>()
        coEvery {
            mockWeatherRepository.getForecastWeather(any(), any())
        } returns mockNetworkForecastWeatherModel
        every { mockWeatherForecastMapper.getForecastWeatherList(any()) } returns expectedForecastWeatherList

        val result = getForecastDataUseCase.invoke(40.0, -74.0).first()

        coVerify {  mockWeatherRepository.getForecastWeather(any(), any()) }
        verify { mockWeatherForecastMapper.getForecastWeatherList(any()) }

        assertEquals(expectedForecastWeatherList.take(5), result)
    }

}
