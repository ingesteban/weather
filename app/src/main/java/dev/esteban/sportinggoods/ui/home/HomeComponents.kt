package dev.esteban.sportinggoods.ui.home

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import dev.esteban.common.utils.ScreenState
import dev.esteban.sportinggoods.R
import dev.esteban.common.network.findActivity
import dev.esteban.common.network.getFormattedSpeed
import dev.esteban.common.network.getHourFormattedDate
import dev.esteban.common.network.getWindDirection
import dev.esteban.common.network.toDegree
import dev.esteban.common.network.toFahrenheitDegree
import dev.esteban.weather.domain.model.CurrentWeather
import dev.esteban.weather.domain.model.CurrentWeatherItem

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val hasLocationGranted = ContextCompat.checkSelfPermission(
        context, ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.getWeatherData(context = context)
        } else {
            val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                context.findActivity(), ACCESS_COARSE_LOCATION
            )
            if (showRationale) {
                viewModel.showErrorNoLocationProvided()
            } else {
                viewModel.showErrorNoLocationProvidedAndDoNotAskAgain()
            }
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isRefreshing,
        onRefresh = { viewModel.onRefresh(context) }
    )

    LaunchedEffect(Unit) {
        if (hasLocationGranted) {
            viewModel.getWeatherData(context = context)
        } else {
            requestPermissionLauncher.launch(ACCESS_COARSE_LOCATION)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
            .padding(bottom = 55.dp)
            .background(colorResource(id = R.color.light_gray))
    ) {
        when {
            uiState.screenState is ScreenState.Loading -> LoadingItem()

            uiState.screenState is ScreenState.Success -> uiState.currentWeather?.let { currentWeather ->
                HomeWeather(currentWeather)
            }

            uiState.screenState is ScreenState.Error -> ErrorScreen(
                homeError = uiState.error,
                onClickButton = {
                    uiState.error?.let { homeError ->
                        if (homeError == HomeError.NO_LOCATION_PROVIDED) {
                            requestPermissionLauncher.launch(ACCESS_COARSE_LOCATION)
                        }
                    }
                }
            )
        }

        PullRefreshIndicator(
            refreshing = uiState.isRefreshing,
            state = pullRefreshState,
            contentColor = colorResource(id = R.color.green_500),
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
fun HomeWeather(
    currentWeather: CurrentWeather,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        HomeHeader(currentWeather)
        HomeWeatherList(currentWeather.weather)
    }
}

@Composable
fun HomeHeader(
    currentWeather: CurrentWeather,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 18.dp, start = 8.dp, end = 8.dp, bottom = 30.dp)
    ) {
        Text(
            text = currentWeather.cityName,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = R.string.label_wind),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Text(
                text = stringResource(
                    id = R.string.label_feels_like,
                    currentWeather.feelsLike.toDegree()
                ),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = currentWeather.windDeg.getWindDirection(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Text(
                text = "${currentWeather.tempMin.toDegree()}/${currentWeather.tempMax.toDegree()}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
        Text(
            text = currentWeather.windSpeed.getFormattedSpeed(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun HomeWeatherList(
    weatherList: List<CurrentWeatherItem>,
    modifier: Modifier = Modifier
) {
    if (weatherList.isNotEmpty()) {
        Column(
            modifier = modifier.padding(horizontal = 2.dp)
        ) {
            weatherList.forEach { currentWeatherItem ->
                CurrentWeatherItem(currentWeatherItem)
            }
        }
    } else {
        Text(
            text = stringResource(id = R.string.no_content_please_refresh),
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)
        )
    }
}

@Composable
fun CurrentWeatherItem(
    currentWeatherItem: CurrentWeatherItem,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth()
            .background(colorResource(id = R.color.green_500))
            .height(50.dp)
            .padding(start = 12.dp, end = 24.dp)
    ) {
        Text(
            text = currentWeatherItem.unixTime.getHourFormattedDate(LocalContext.current),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .align(Alignment.CenterStart)
        )
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentWeatherItem.temp.toFahrenheitDegree(),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
            AsyncImage(
                model = "https://openweathermap.org/img/wn/${currentWeatherItem.icon}@2x.png",
                contentDescription = null,
                modifier = Modifier.padding(start = 24.dp)
            )
        }
    }
}

@Composable
fun LoadingItem(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier
                .width(64.dp)
                .align(Alignment.Center),
            color = colorResource(id = R.color.green_500),
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    homeError: HomeError? = null,
    onClickButton: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {
            Text(
                text = getErrorMessage(homeError),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                modifier = Modifier
                    .fillMaxWidth()
            )
            homeError?.let {
                if (homeError == HomeError.NO_LOCATION_PROVIDED) {
                    Button(
                        modifier = Modifier
                            .padding(top = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.green_500),
                            contentColor = Color.White
                        ),
                        onClick = onClickButton
                    ) {
                        Text(
                            color = Color.White,
                            text = stringResource(id = R.string.button_give_location_permissions)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun getErrorMessage(homeError: HomeError? = null): String = homeError?.let {
    val errorResourceMessage = when (homeError) {
        HomeError.NO_LOCATION_PROVIDED -> R.string.error_no_location_provided
        HomeError.NO_LOCATION_PROVIDED_DO_NOT_ASK_AGAIN -> R.string.error_no_location_provided_do_not_ask_again
        HomeError.GENERIC -> R.string.error_generic
    }
    stringResource(id = errorResourceMessage)
} ?: run {
    String()
}
