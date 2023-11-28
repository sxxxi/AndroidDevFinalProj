package seiji.prog39402finalproject.data.remote.firestore

import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import seiji.prog39402finalproject.data.remote.firestore.constants.CapsuleCollection
import seiji.prog39402finalproject.data.remote.models.CapsuleRemoteModel

class CapsuleRemoteDataSourceImpl : CapsuleRemoteDataSource {
    private val capsules = Firebase.firestore.collection(CapsuleCollection.name)

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

        bounds.forEach { bound ->
           final = final
                .whereGreaterThanOrEqualTo("geoHash", bound.startHash)
                .whereLessThanOrEqualTo("geoHash", bound.endHash)
        }

        final.get()
            .addOnSuccessListener { doc ->
                onSuccess(doc.documents.map { CapsuleRemoteModel.from(it) })
            }
            .addOnFailureListener(onFailure)
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
}