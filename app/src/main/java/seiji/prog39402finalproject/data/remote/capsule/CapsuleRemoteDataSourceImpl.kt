package seiji.prog39402finalproject.data.remote.capsule

import android.util.Log
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import seiji.prog39402finalproject.data.remote.models.CapsuleRemoteModel

class CapsuleRemoteDataSourceImpl : CapsuleRemoteDataSource {
    private val capsules = Firebase.firestore.collection("capsule")

    override fun getNearbyCapsules(
        center: LatLng,
        radiusM: Double,
        onSuccess: (List<CapsuleRemoteModel>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {

        val bounds = GeoFireUtils.getGeoHashQueryBounds(
            GeoLocation(center.latitude, center.longitude),
            radiusM
        )

        var final = capsules.orderBy("geoHash")

        bounds.forEachIndexed { index, it ->
            Log.d(TAG, "Idx: $index, Hash: ${it.startHash} ~ ${it.endHash}")
        }

        bounds.forEach { bound ->
            final = final
                .startAt(bound.startHash)
                .endAt(bound.endHash)
        }

        val tasks = bounds.map { bound ->
            final
                .startAt(bound.startHash)
                .endAt(bound.endHash)
                .get()
        }

        Tasks.whenAllComplete(tasks).addOnCompleteListener {
            if (it.isSuccessful) {
                val centerG = GeoLocation(center.latitude, center.longitude)
                var allCapsules = mutableListOf<CapsuleRemoteModel>()
                tasks.forEach { task ->
                    // Capture all received capsules
                    allCapsules += task.result.documents.map { doc -> CapsuleRemoteModel.from(doc) }
                }

                // Filter out false positives
                allCapsules = allCapsules.filter { capsule ->
                    val capsulePoint =
                        GeoLocation(capsule.coord.latitude, capsule.coord.longitude)
                    val dist = GeoFireUtils.getDistanceBetween(capsulePoint, centerG)
                    dist <= radiusM
                }.toMutableList()

                onSuccess(allCapsules)
            } else {
                onFailure(it.exception!!)
            }
        }
    }

    override fun createCapsule(
        capsule: CapsuleRemoteModel,
        onSuccess: (DocumentReference) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        capsules.add(capsule.toFirestoreMap())
            .addOnSuccessListener(onSuccess)
            .addOnFailureListener(onFailure)
    }

    companion object {
        private const val TAG = "CapsuleRemoteDataSourceImpl"
    }
}