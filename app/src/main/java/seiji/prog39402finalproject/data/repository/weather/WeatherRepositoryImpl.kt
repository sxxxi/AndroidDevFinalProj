package seiji.prog39402finalproject.data.repository.weather

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.await
import retrofit2.awaitResponse
import seiji.prog39402finalproject.data.remote.models.WeatherRemoteModel
import seiji.prog39402finalproject.data.remote.weather.WeatherRemoteDataSource
import java.io.IOException

class WeatherRepositoryImpl(
    private val weatherRemoteDataSource: WeatherRemoteDataSource
) {
    suspend fun getCoordinateWeather(
        coordinate: LatLng,
        onSuccess: (WeatherRemoteModel) -> Unit,
        onError: (Throwable) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            val call = weatherRemoteDataSource.weatherAtCoordinates(
                lat = coordinate.latitude,
                lon = coordinate.longitude
            )

            Log.d("HTTPREQUEST", "${call.request().body()}")
            val res = call.await()

            onSuccess(res)
        } catch (e: HttpException) {
            onError(e)
        }
    }
}