package dev.esteban.common.network

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dev.esteban.common.network.Constants.BASE_URL
import dev.esteban.common.network.Constants.WEATHER_KEY
import kotlinx.serialization.ExperimentalSerializationApi
import retrofit2.create
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitBuilder @Inject constructor(private val networkJson: Json) {

    companion object {
        private const val APP_ID = "appid"
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun getClient(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(
            OkHttpClient.Builder()
                .addInterceptor(
                    HttpLoggingInterceptor().apply {
                        setLevel(HttpLoggingInterceptor.Level.BODY)
                    }
                )
                .addInterceptor { chain ->
                    val original = chain.request()
                    val originalHttpUrl = original.url
                    val url = originalHttpUrl.newBuilder()
                        .addQueryParameter(APP_ID, WEATHER_KEY)
                        .build()
                    val requestBuilder = original.newBuilder()
                        .url(url)
                    val request = requestBuilder.build()
                    chain.proceed(request)
                }
                .build()
        )
        .addConverterFactory(
            networkJson.asConverterFactory("application/json".toMediaType())
        )
        .build()

    inline fun <reified T> create(): T {
        return getClient().create(T::class.java)
    }
}