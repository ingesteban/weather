package dev.esteban.weather.domain.usecase

import dev.esteban.weather.data.datasource.remote.model.NetworkCityModel
import dev.esteban.weather.data.datasource.remote.model.NetworkCloudModel
import dev.esteban.weather.data.datasource.remote.model.NetworkCoordModel
import dev.esteban.weather.data.datasource.remote.model.NetworkForecastSysModel
import dev.esteban.weather.data.datasource.remote.model.NetworkForecastWeatherInfoModel
import dev.esteban.weather.data.datasource.remote.model.NetworkForecastWeatherModel
import dev.esteban.weather.data.datasource.remote.model.NetworkMainModel
import dev.esteban.weather.data.datasource.remote.model.NetworkSysModel
import dev.esteban.weather.data.datasource.remote.model.NetworkWeatherDataModel
import dev.esteban.weather.data.datasource.remote.model.NetworkWeatherDetailModel
import dev.esteban.weather.data.datasource.remote.model.NetworkWindModel
import dev.esteban.weather.data.repository.WeatherRepository
import dev.esteban.weather.domain.model.CurrentWeather
import dev.esteban.weather.domain.model.CurrentWeatherItem
import dev.esteban.weather.domain.model.ForecastWeather
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GetWeatherDataUseCaseTest {
    private val mockWeatherRepository: WeatherRepository = mockk()
    private lateinit var getWeatherDataUseCase: GetWeatherDataUseCase

    @Before
    fun setupBase() {
        MockKAnnotations.init(this)
        getWeatherDataUseCase = GetWeatherDataUseCase(mockWeatherRepository)
    }

    @After
    fun tearDown() {
        confirmVerified(mockWeatherRepository)
    }

    @Test
    fun `test invoke returns NetworkForecastWeatherModel`() = runBlocking {
        val dummyLatitude = 40.7128
        val dummyLongitude = -74.0060
        val startOfCurrentDay = 1234567890L
        val endOfCurrentDay = 1234569890L
        val fakeNetworkWeatherDataModel = createFakeNetworkWeatherDataModel()
        val fakeNetworkForecastWeatherModel = createFakeNetworkForecastWeatherModel()
        coEvery {
            mockWeatherRepository.getCurrentWeather(
                dummyLatitude,
                dummyLongitude
            )
        } returns fakeNetworkWeatherDataModel
        coEvery {
            mockWeatherRepository.getForecastWeather(
                dummyLatitude,
                dummyLongitude
            )
        } returns fakeNetworkForecastWeatherModel

        val result = getWeatherDataUseCase(
            dummyLatitude,
            dummyLongitude,
            startOfCurrentDay,
            endOfCurrentDay
        ).first()

        val expectedResult = CurrentWeather(
            tempMin = fakeNetworkWeatherDataModel.main.tempMin,
            tempMax = fakeNetworkWeatherDataModel.main.tempMax,
            feelsLike = fakeNetworkWeatherDataModel.main.feelsLike,
            windDeg = fakeNetworkWeatherDataModel.wind.deg,
            windSpeed = fakeNetworkWeatherDataModel.wind.speed,
            cityName = fakeNetworkWeatherDataModel.name,
            weather = listOf()
        )
        Assert.assertEquals(expectedResult, result)
        coVerify { mockWeatherRepository.getForecastWeather(dummyLatitude, dummyLongitude) }
        coVerify { mockWeatherRepository.getCurrentWeather(dummyLatitude, dummyLongitude) }
    }

    @Test
    fun `test invoke returns NetworkForecastWeatherMsodel`() = runBlocking {
        val dummyLatitude = 40.7128
        val dummyLongitude = -74.0060
        val startOfCurrentDay = 1234567890L
        val endOfCurrentDay = 1234569890L
        val fakeNetworkWeatherDataModel = createFakeNetworkWeatherDataModel()
        val fakeNetworkForecastWeatherModel = createFakeNetworkForecastWeatherModel()
        coEvery {
            mockWeatherRepository.getCurrentWeather(
                dummyLatitude,
                dummyLongitude
            )
        } returns fakeNetworkWeatherDataModel
        coEvery {
            mockWeatherRepository.getForecastWeather(
                dummyLatitude,
                dummyLongitude
            )
        } returns fakeNetworkForecastWeatherModel

        val result = getWeatherDataUseCase(
            dummyLatitude,
            dummyLongitude,
            startOfCurrentDay,
            endOfCurrentDay
        ).first()

        val expectedResult = CurrentWeather(
            tempMin = fakeNetworkWeatherDataModel.main.tempMin,
            tempMax = fakeNetworkWeatherDataModel.main.tempMax,
            feelsLike = fakeNetworkWeatherDataModel.main.feelsLike,
            windDeg = fakeNetworkWeatherDataModel.wind.deg,
            windSpeed = fakeNetworkWeatherDataModel.wind.speed,
            cityName = fakeNetworkWeatherDataModel.name,
            weather = listOf()
        )
        Assert.assertEquals(expectedResult, result)
        coVerify { mockWeatherRepository.getForecastWeather(dummyLatitude, dummyLongitude) }
        coVerify { mockWeatherRepository.getCurrentWeather(dummyLatitude, dummyLongitude) }
    }

    private fun createFakeNetworkWeatherDataModel(): NetworkWeatherDataModel {
        return NetworkWeatherDataModel(
            coord = NetworkCoordModel(lon = 10.0, lat = 20.0),
            weather = listOf(
                NetworkWeatherDetailModel(
                    id = 800,
                    main = "Clear",
                    description = "Clear sky",
                    icon = "01d"
                )
            ),
            base = "stations",
            main = NetworkMainModel(
                temp = 25.0,
                feelsLike = 27.0,
                tempMin = 22.0,
                tempMax = 28.0,
                pressure = 1015,
                humidity = 60
            ),
            visibility = 10000,
            wind = NetworkWindModel(speed = 5.0, deg = 120),
            clouds = NetworkCloudModel(all = 0),
            dt = 1632316800,
            sys = NetworkSysModel(
                type = 1,
                id = 1234,
                country = "US",
                sunrise = 1632300000,
                sunset = 1632340000
            ),
            timezone = -18000,
            id = 1234567,
            name = "FakeCity",
            cod = 200
        )
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
