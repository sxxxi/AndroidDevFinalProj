package seiji.prog39402finalproject.presentation.drop

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.launch
import seiji.prog39402finalproject.data.repository.CapsuleFirestoreRepository
import seiji.prog39402finalproject.data.repository.CapsuleFirestoreRepositoryImpl
import seiji.prog39402finalproject.domain.Capsule
import seiji.prog39402finalproject.domain.forms.CapsuleCreateForm

class DropViewModel(
    private val capsuleRepository: CapsuleFirestoreRepository = CapsuleFirestoreRepositoryImpl()
) : ViewModel() {

    private val mutNewCapsule = MutableLiveData(Capsule())
    val newCapsule: LiveData<Capsule> = mutNewCapsule

    private val mutCapsuleImages = MutableLiveData<List<Bitmap>>(listOf())
    val capsuleImages: LiveData<List<Bitmap>> = mutCapsuleImages


    fun updateCapsule(updater: (Capsule) -> Capsule) {
        newCapsule.value?.let { copy ->
            mutNewCapsule.value = updater(copy)
        }
    }

    fun updateCapsulePos(pos: LatLng) {
        val hash = GeoFireUtils.getGeoHashForLocation(GeoLocation(pos.latitude, pos.longitude),9)
        updateCapsule { it.copy(
            geoHash = hash,
            coord = pos
        ) }
    }

    fun queueImage(bitmap: Bitmap) {
        mutCapsuleImages.value?.let { images ->
            mutCapsuleImages.value = images.toMutableList() + bitmap
        }
    }

    fun createCapsule(
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val capsule = newCapsule.value
        val images = capsuleImages.value

        if (capsule == null || images == null) return

        viewModelScope.launch {
            capsuleRepository.dropCapsule(
                CapsuleCreateForm(
                    newCapsule = capsule,
                    images = images
                ),
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        }
    }
}