package dev.esteban.weather.data.repository

import dev.esteban.weather.data.datasource.remote.WeatherNetworkDataSource
import dev.esteban.weather.data.datasource.remote.model.NetworkForecastWeatherModel
import dev.esteban.weather.data.datasource.remote.model.NetworkWeatherDataModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherRepositoryImplTest {

    private companion object {
        const val LAT = 44.34
        const val LON = 10.99
    }

    private val weatherNetworkDataSource: WeatherNetworkDataSource = mockk()
    private lateinit var repositoryImpl: WeatherRepositoryImpl

    @Before
    fun setupBase() {
        MockKAnnotations.init(this)
        repositoryImpl = WeatherRepositoryImpl(weatherNetworkDataSource)
    }

    @Test
    fun shouldGetCurrentWeatherSuccess() {
        val response: NetworkWeatherDataModel = mockk(relaxed = true)
        var result: NetworkWeatherDataModel? = null

        coEvery { weatherNetworkDataSource.getCurrentWeather(LAT, LON) } returns response

        runTest(UnconfinedTestDispatcher()) {
            result = repositoryImpl.getCurrentWeather(LAT, LON)
        }

        TestCase.assertNotNull(result)
        TestCase.assertEquals(result, response)
        coVerify { weatherNetworkDataSource.getCurrentWeather(LAT, LON) }
        verify { response.equals(result) }
        confirmVerified(weatherNetworkDataSource, response)
    }

    @Test
    fun shouldGetCurrentWeatherError() {

        coEvery { weatherNetworkDataSource.getCurrentWeather(LAT, LON) } throws Exception("error")

        var exceptionThrown = false
        try {
            runTest(UnconfinedTestDispatcher()) {
                repositoryImpl.getCurrentWeather(LAT, LON)
            }
        } catch (exception: Exception) {
            exceptionThrown = true
        }
        assertTrue(exceptionThrown)
    }

    @Test
    fun shouldGetForecastWeatherSuccess() {
        val response: NetworkForecastWeatherModel = mockk(relaxed = true)
        var result: NetworkForecastWeatherModel? = null

        coEvery { weatherNetworkDataSource.getForecastWeather(LAT, LON) } returns response

        runTest(UnconfinedTestDispatcher()) {
            result = repositoryImpl.getForecastWeather(LAT, LON)
        }

        TestCase.assertNotNull(result)
        TestCase.assertEquals(result, response)
        coVerify { weatherNetworkDataSource.getForecastWeather(LAT, LON) }
        verify { response.equals(result) }
        confirmVerified(weatherNetworkDataSource, response)
    }

    @Test
    fun shouldGetForecastWeatherError() {

        coEvery { weatherNetworkDataSource.getForecastWeather(LAT, LON) } throws Exception("error")

        var exceptionThrown = false
        try {
            runTest(UnconfinedTestDispatcher()) {
                repositoryImpl.getForecastWeather(LAT, LON)
            }
        } catch (exception: Exception) {
            exceptionThrown = true
        }
        assertTrue(exceptionThrown)
    }
}
