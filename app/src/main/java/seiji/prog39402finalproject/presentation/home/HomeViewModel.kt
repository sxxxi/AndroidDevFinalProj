package seiji.prog39402finalproject.presentation.home

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import seiji.prog39402finalproject.data.mappers.CapsuleMapper
import seiji.prog39402finalproject.data.remote.models.WeatherRemoteModel
import seiji.prog39402finalproject.data.repository.capsule.CapsuleFirestoreRepository
import seiji.prog39402finalproject.data.repository.capsule.CapsuleFirestoreRepositoryImpl
import seiji.prog39402finalproject.data.repository.weather.WeatherRepositoryImpl
import seiji.prog39402finalproject.domain.constants.NetworkServices
import seiji.prog39402finalproject.domain.models.Capsule

class HomeViewModel(
    private val capsuleRepo: CapsuleFirestoreRepository = CapsuleFirestoreRepositoryImpl(),
    private val capsuleMapper: CapsuleMapper = CapsuleMapper(),
    private val weatherRepositoryImpl: WeatherRepositoryImpl = WeatherRepositoryImpl(NetworkServices.weatherService)
) : ViewModel() {
    private var _currentLocation = MutableLiveData(LatLng(0.0, 0.0))
    val currentLocation: LiveData<LatLng> = _currentLocation

    private var lastRequestCoordinates: LatLng = LatLng(0.0, 0.0)

    private var _nearbyCapsules = MutableLiveData<List<Capsule>>(listOf())
    val nearbyCapsules: LiveData<List<Capsule>> = _nearbyCapsules

    private var _selectedCapsule: MutableLiveData<Capsule?> = MutableLiveData(null)
    val selectedCapsule: LiveData<Capsule?> = _selectedCapsule

    private val _currentWeather = MutableLiveData<WeatherRemoteModel?>(null)
    val currentWeather: LiveData<WeatherRemoteModel?> = _currentWeather

    fun updateLocation(newLoc: LatLng) {
        _currentLocation.value = newLoc
    }

    fun attemptGetNearbyCapsules(
        center: LatLng,
        radiusM: Double = 400.0
    ) {
        viewModelScope.launch {
            capsuleRepo.getNearbyCapsules(
                center = center,
                radiusM = radiusM,
                onSuccess = { capsules ->
                    Log.d("FIRESTORE", capsules.toString())
                    lastRequestCoordinates = center
                    _nearbyCapsules.value = capsules.map { cap -> capsuleMapper.toDomain(cap) }
                },
                onFailure = {
                    Log.e("FIRESTORE", "$it")
                }
            )
        }
    }

    fun setFocusedCapsule(capsule: Capsule?) {
        _selectedCapsule.value = capsule
    }

    fun getCapsuleImages(
        capsule: Capsule,
        onImageReady: (List<Bitmap>) -> Unit
    ) {
        capsuleRepo.getImagesFromLinks(capsule.images, onImageReady)
    }

    fun getWeather(coordinates: LatLng) {
        viewModelScope.launch {
            weatherRepositoryImpl.getCoordinateWeather(
                coordinate = coordinates,
                onSuccess = {
                    Log.d(TAG, "SUCCESS!!: $it")
                },
                onError = {
                    Log.e(TAG, "Error: $it")
                }
            )
        }
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}