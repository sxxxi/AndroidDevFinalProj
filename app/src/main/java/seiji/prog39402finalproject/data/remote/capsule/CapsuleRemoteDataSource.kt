package seiji.prog39402finalproject.data.remote.capsule

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentReference
import seiji.prog39402finalproject.data.remote.models.CapsuleRemoteModel

interface CapsuleRemoteDataSource {
    fun getNearbyCapsules(
        center: LatLng,
        radiusM: Double = 5.0,
        onSuccess: (List<CapsuleRemoteModel>) -> Unit,
        onFailure: (Throwable) -> Unit
    )

    fun createCapsule(
        capsule: CapsuleRemoteModel,
        onSuccess: (DocumentReference) -> Unit = {},
        onFailure: (Throwable) -> Unit = {}
    )
}