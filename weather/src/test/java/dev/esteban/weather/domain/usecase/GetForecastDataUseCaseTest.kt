package dev.esteban.weather.domain.usecase

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
import dev.esteban.weather.domain.model.ForecastWeather
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetForecastDataUseCaseTest {
    private val mockWeatherRepository: WeatherRepository = mockk()
    private lateinit var getForecastDataUseCase: GetForecastDataUseCase

    @Before
    fun setupBase() {
        MockKAnnotations.init(this)
        getForecastDataUseCase = GetForecastDataUseCase(mockWeatherRepository)
    }

    @After
    fun tearDown() {
        confirmVerified(mockWeatherRepository)
    }

    @Test
    fun `test invoke returns NetworkForecastWeatherModel`() = runBlocking {
        val dummyLatitude = 40.7128
        val dummyLongitude = -74.0060
        val fakeNetworkForecastWeatherModel = createFakeNetworkForecastWeatherModel()
        coEvery {
            mockWeatherRepository.getForecastWeather(
                dummyLatitude,
                dummyLongitude
            )
        } returns fakeNetworkForecastWeatherModel

        val result = getForecastDataUseCase(dummyLatitude, dummyLongitude).first()

        val expectedResult = fakeNetworkForecastWeatherModel.list.map {
            ForecastWeather(
                highTemp = it.main.tempMax,
                lowTemp = it.main.tempMin,
                date = it.dt,
                windSpeed = it.wind.speed,
                windDirection = it.wind.deg,
                windDescription = it.weather.first().description,
                icon = it.weather.first().icon
            )
        }
        assertEquals(expectedResult, result)
        coVerify { mockWeatherRepository.getForecastWeather(dummyLatitude, dummyLongitude) }
    }

    private fun createFakeNetworkForecastWeatherModel(): NetworkForecastWeatherModel {
        val fakeCity = NetworkCityModel(
            id = 1,
            name = "Fake City",
            coord = NetworkCoordModel(lon = 0.0, lat = 0.0),
            country = "FC",
            population = 100000,
            timezone = 0,
            sunrise = 0,
            sunset = 0
        )

        val fakeWeatherInfoModel = NetworkForecastWeatherInfoModel(
            dt = 1234567890,
            main = NetworkMainModel(
                temp = 25.0,
                feelsLike = 28.0,
                tempMin = 20.0,
                tempMax = 30.0,
                pressure = 1013,
                humidity = 50
            ),
            weather = listOf(
                NetworkWeatherDetailModel(
                    id = 800,
                    main = "Clear",
                    description = "clear sky",
                    icon = "01d"
                )
            ),
            clouds = NetworkCloudModel(all = 0),
            wind = NetworkWindModel(speed = 3.0, deg = 120),
            visibility = 10000,
            pop = 0.0,
            sys = NetworkForecastSysModel(pod = "d"),
            dtTxt = "2023-01-01 12:00:00"
        )

        return NetworkForecastWeatherModel(
            cod = "200",
            message = 0,
            cnt = 1,
            list = listOf(
                fakeWeatherInfoModel,
                fakeWeatherInfoModel,
                fakeWeatherInfoModel,
                fakeWeatherInfoModel,
                fakeWeatherInfoModel
            ),
            city = fakeCity
        )
    }
}
