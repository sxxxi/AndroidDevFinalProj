package seiji.prog39402finalproject.presentation.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import seiji.prog39402finalproject.data.mappers.CapsuleMapper
import seiji.prog39402finalproject.data.remote.firestore.CapsuleRemoteDataSource
import seiji.prog39402finalproject.data.remote.firestore.CapsuleRemoteDataSourceImpl
import seiji.prog39402finalproject.data.remote.firestore.ImageStoreRemoteDataSource
import seiji.prog39402finalproject.data.remote.firestore.ImageStoreRemoteDataSourceImpl
import seiji.prog39402finalproject.data.repository.CapsuleFirestoreRepository
import seiji.prog39402finalproject.data.repository.CapsuleFirestoreRepositoryImpl
import seiji.prog39402finalproject.domain.Capsule

class HomeViewModel(
    private val capsuleRepo: CapsuleFirestoreRepository = CapsuleFirestoreRepositoryImpl(),
    private val capsuleMapper: CapsuleMapper = CapsuleMapper()
) : ViewModel() {
    private var mutCurrentLocation = MutableLiveData(LatLng(0.0, 0.0))
    val currentLocation: LiveData<LatLng> = mutCurrentLocation

    private var lastRequestCoordinates: LatLng = LatLng(0.0, 0.0)

    private var mutNearbyCapsules = MutableLiveData<List<Capsule>>(listOf())
    val nearbyCapsules: LiveData<List<Capsule>> = mutNearbyCapsules

    private var mutSelectedCapsule: MutableLiveData<Capsule?> = MutableLiveData(null)
    val selectedCapsule: LiveData<Capsule?> = mutSelectedCapsule

    fun updateLocation(newLoc: LatLng) {
        mutCurrentLocation.value = newLoc
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
                    mutNearbyCapsules.value = capsules.map { cap -> capsuleMapper.toDomain(cap) }
                },
                onFailure = {
                    Log.e("FIRESTORE", "$it")
                }
            )
        }
    }

    fun setFocusedCapsule(capsule: Capsule?) {
        mutSelectedCapsule.value = capsule
    }

    fun getCapsuleImages(
        capsule: Capsule,
        onImageReady: (List<Bitmap>) -> Unit
    ) {
        capsuleRepo.getImagesFromLinks(capsule.images, onImageReady)
    }
}