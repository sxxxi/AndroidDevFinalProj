package seiji.prog39402finalproject.presentation.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import seiji.prog39402finalproject.data.remote.models.WeatherRemoteModel
import seiji.prog39402finalproject.data.repository.weather.WeatherRepositoryImpl
import seiji.prog39402finalproject.domain.constants.NetworkServices

class WeatherViewModel(
    private val weatherRepositoryImpl: WeatherRepositoryImpl = WeatherRepositoryImpl(NetworkServices.weatherService)
) : ViewModel() {

    private val _currentWeather = MutableLiveData<WeatherRemoteModel.Weather?>(null)
    val currentWeather: LiveData<WeatherRemoteModel.Weather?> = _currentWeather

    fun getWeather(
        coordinates: LatLng,
    ) {
        viewModelScope.launch {
            weatherRepositoryImpl.getCoordinateWeather(
                coordinate = coordinates,
                onSuccess = {
                    viewModelScope.launch(Dispatchers.Main) {
                        _currentWeather.value = it.weather.firstOrNull()
                    }
                },
                onError = {}
            )
        }
    }
}