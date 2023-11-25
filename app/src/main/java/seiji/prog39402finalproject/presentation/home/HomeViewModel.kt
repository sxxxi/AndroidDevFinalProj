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

    private var mutNearbyCapsules = MutableLiveData<List<Capsule>>(listOf())
    val nearbyCapsules: LiveData<List<Capsule>> = mutNearbyCapsules


    init {
        // Refresh nearby capsules every 10 seconds
        viewModelScope.launch {

        }
    }


    fun updateLocation(newLoc: LatLng) {
        mutCurrentLocation.value = newLoc
    }

    fun attemptGetNearbyCapsules(
        center: LatLng,
    ) {
        val deltaDist = lastRequestCoordinates.getDistance(center)
        Log.d("NearbyCapsules", "$deltaDist")
        if (deltaDist <= 5E-5) return
        Log.d("NearbyCapsules", "Im in")

        capsuleRemoteRepository.getNearbyCapsules(
            center = center,
            onSuccess = {
                lastRequestCoordinates = center
                mutNearbyCapsules.value = it.map { cap -> capsuleMapper.toDomain(cap) }
            },
            onFailure = {}
        )
    }
}