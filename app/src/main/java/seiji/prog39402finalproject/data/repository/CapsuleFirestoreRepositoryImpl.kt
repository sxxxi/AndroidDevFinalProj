package seiji.prog39402finalproject.data.repository

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.type.LatLng
import seiji.prog39402finalproject.data.remote.models.CapsuleRemoteModel
import seiji.prog39402finalproject.domain.Capsule

class CapsuleFirestoreRepositoryImpl : CapsuleFirestoreRepository {
    override suspend fun putCapsule(capsule: CapsuleRemoteModel) {
        val db = Firebase.firestore

        db.collection("capsule")
            .add(capsule.toFirestoreMap())
            .addOnSuccessListener {
                Log.d("Suc", "SUCCESS")
            }
            .addOnFailureListener {
                Log.d("Suc", "FAILURE")
            }
    }

    override suspend fun getNearbyCapsules(coord: LatLng): List<Capsule> {

        return listOf()
    }
}