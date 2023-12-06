package seiji.prog39402finalproject.domain.constants

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import seiji.prog39402finalproject.data.remote.weather.WeatherRemoteDataSource

object NetworkServices {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val weatherService: WeatherRemoteDataSource = Retrofit
        .Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .build().create(WeatherRemoteDataSource::class.java)
}