package seiji.prog39402finalproject.data.repository

import com.google.type.LatLng
import seiji.prog39402finalproject.data.remote.models.CapsuleRemoteModel
import seiji.prog39402finalproject.domain.Capsule

interface CapsuleFirestoreRepository {
    suspend fun putCapsule(capsule: CapsuleRemoteModel)
    suspend fun getNearbyCapsules(coord: LatLng): List<Capsule>
}