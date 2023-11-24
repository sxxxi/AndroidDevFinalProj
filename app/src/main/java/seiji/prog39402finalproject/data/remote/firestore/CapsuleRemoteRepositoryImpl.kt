package seiji.prog39402finalproject.data.remote.firestore

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import seiji.prog39402finalproject.data.remote.firestore.constants.CapsuleCollection
import seiji.prog39402finalproject.data.remote.models.CapsuleRemoteModel
import kotlin.math.cos

class CapsuleRemoteRepositoryImpl : CapsuleRemoteRepository {
    private val capsules = Firebase.firestore.collection(CapsuleCollection.name)

    override fun getNearbyCapsules(
        center: LatLng,
        onSuccess: (List<CapsuleRemoteModel>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {

        // Get Longitude and latitude 5m
        val coef = 10 / 111320.00
        val latMax = center.latitude + coef
        val latMin = center.latitude - coef
        val lonMax = center.longitude + coef / cos(Math.PI/180)
        val lonMin = center.longitude - coef / cos(Math.PI/180)

        Log.d("COORD", "($latMax, $lonMin)")

        capsules
            .whereLessThanOrEqualTo("coord.latitude", latMax)
            .whereGreaterThanOrEqualTo("coord.latitude", latMin)
            .get()
            .addOnSuccessListener { snapshot ->
                // Filter on longitude locally
                val filteredDocuments = snapshot.documents.filter { document ->
                    val docLongitude = document.getDouble("coord.longitude") ?: 0.0
                    docLongitude in lonMin..lonMax
                }
                onSuccess(
                    filteredDocuments.map {
                        CapsuleRemoteModel.from(it)
                    }
                )
            }
            .addOnFailureListener {
                onFailure(it)
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
}