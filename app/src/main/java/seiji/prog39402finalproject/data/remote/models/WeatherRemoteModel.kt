package seiji.prog39402finalproject.data.remote.models

import com.squareup.moshi.Json

data class WeatherRemoteModel(
    @Json(name = "weather") val weather: List<Weather>
) {
    data class Weather(
        @Json(name = "id") val id: Int,
        @Json(name = "main") val main: String,
        @Json(name = "description") val description: String
    )
}

