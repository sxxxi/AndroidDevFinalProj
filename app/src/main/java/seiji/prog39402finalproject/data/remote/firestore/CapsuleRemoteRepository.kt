package seiji.prog39402finalproject.data.remote.firestore

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentReference
import seiji.prog39402finalproject.data.remote.models.CapsuleRemoteModel

interface CapsuleRemoteRepository {
    fun getNearbyCapsules(
        center: LatLng,
        onSuccess: (List<CapsuleRemoteModel>) -> Unit,
        onFailure: (Throwable) -> Unit
    )
    fun createCapsule(
        capsule: CapsuleRemoteModel,
        onSuccess: (DocumentReference) -> Unit,
        onFailure: (Throwable) -> Unit
    )
}