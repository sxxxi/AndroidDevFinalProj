package seiji.prog39402finalproject.data.remote.weather

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import seiji.prog39402finalproject.BuildConfig
import seiji.prog39402finalproject.data.remote.models.WeatherRemoteModel

const val API_KEY = BuildConfig.WEATHER_API_KEY

interface WeatherRemoteDataSource {
    @GET("weather")
    fun weatherAtCoordinates(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String = API_KEY
    ): Call<WeatherRemoteModel>
}