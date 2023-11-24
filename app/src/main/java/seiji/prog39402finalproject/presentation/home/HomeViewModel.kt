package seiji.prog39402finalproject.presentation.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import seiji.prog39402finalproject.data.mappers.CapsuleMapper
import seiji.prog39402finalproject.data.remote.firestore.CapsuleRemoteRepository
import seiji.prog39402finalproject.data.remote.firestore.CapsuleRemoteRepositoryImpl
import seiji.prog39402finalproject.data.remote.models.CapsuleRemoteModel
import seiji.prog39402finalproject.domain.Capsule
import seiji.prog39402finalproject.presentation.extensions.getDistance
import kotlin.math.cos

class HomeViewModel(
    private val capsuleRemoteRepository: CapsuleRemoteRepository = CapsuleRemoteRepositoryImpl(),
    private val capsuleMapper: CapsuleMapper = CapsuleMapper()
) : ViewModel() {
    private var mutCurrentLocation = MutableLiveData(LatLng(0.0, 0.0))
    val currentLocation: LiveData<LatLng> = mutCurrentLocation

    private var lastRequestCoordinates: LatLng = LatLng(0.0, 0.0)

    private var mutNearbyCapsules = MutableLiveData<List<CapsuleRemoteModel>>(listOf())
    val nearbyCapsules: LiveData<List<CapsuleRemoteModel>> = mutNearbyCapsules


    init {
        // Refresh nearby capsules every 10 seconds
        viewModelScope.launch {

        }
    }


    fun updateLocation(newLoc: LatLng) {
        mutCurrentLocation.value = newLoc
    }

    fun bounds(center: LatLng): Array<LatLng> {
        val coef = 10 / 111320.00
        val lat_max = center.latitude + coef
        val lat_min = center.latitude - coef
        val lon_max = center.longitude + coef / cos(Math.PI/180)
        val lon_min = center.longitude - coef / cos(Math.PI/180)

        return arrayOf(
            LatLng(lat_max, lon_max),
            LatLng(lat_max, lon_min),
            LatLng(lat_min, lon_min),
            LatLng(lat_min, lon_max),
            LatLng(lat_max, lon_max),
        )
    }

    fun attemptGetNearbyCapsules(
        center: LatLng,
        onSuccess: (List<Capsule>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val deltaDist = lastRequestCoordinates.getDistance(center)
        Log.d("NearbyCapsules", "$deltaDist")
        if (deltaDist <= 5E-5) return
        Log.d("NearbyCapsules", "Im in")

        capsuleRemoteRepository.getNearbyCapsules(
            center = center,
            onSuccess = {
                lastRequestCoordinates = center
                onSuccess(it.map { cap -> capsuleMapper.toDomain(cap) })
            },
            onFailure = onFailure
        )
    }
}