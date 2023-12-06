package seiji.prog39402finalproject.data.repository.capsule

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import seiji.prog39402finalproject.data.remote.models.CapsuleRemoteModel
import seiji.prog39402finalproject.domain.forms.CapsuleCreateForm

interface CapsuleFirestoreRepository {
    suspend fun getNearbyCapsules(
        center: LatLng,
        radiusM: Double = 500.0,
        onSuccess: (List<CapsuleRemoteModel>) -> Unit = {},
        onFailure: (Throwable) -> Unit = {}
    )

    fun getImagesFromLinks(
        imageLinks: List<String>,
        onImagesLoaded: (List<Bitmap>) -> Unit
    )

    suspend fun dropCapsule(
        capsuleForm: CapsuleCreateForm,
        onSuccess: () -> Unit = {},
        onFailure: (Throwable) -> Unit = {}
    )
}