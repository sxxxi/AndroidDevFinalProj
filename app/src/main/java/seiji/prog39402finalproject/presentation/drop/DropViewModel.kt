package seiji.prog39402finalproject.presentation.drop

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import seiji.prog39402finalproject.data.remote.firestore.CapsuleRemoteRepository
import seiji.prog39402finalproject.data.remote.firestore.CapsuleRemoteRepositoryImpl
import seiji.prog39402finalproject.data.remote.models.CapsuleRemoteModel
import seiji.prog39402finalproject.domain.Capsule
import kotlin.math.cos

class DropViewModel(
    private val capsuleRemoteRepository: CapsuleRemoteRepository = CapsuleRemoteRepositoryImpl()
) : ViewModel() {

    private val mutNewCapsule = MutableLiveData(CapsuleRemoteModel.create())
    val newCapsule: LiveData<CapsuleRemoteModel> = mutNewCapsule


    fun updateCapsule(updater: (CapsuleRemoteModel) -> CapsuleRemoteModel) {
        newCapsule.value?.let { copy ->
            mutNewCapsule.value = updater(copy)
        }
    }

    fun createCapsule(
        onSuccess: (DocumentReference) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        // Validate fields here but ain't nobody got time for that
        newCapsule.value?.let { newCap ->
            capsuleRemoteRepository.createCapsule(
                capsule = newCap,
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        }
    }

    fun getNearbyCapsules(coordinates: LatLng) {
        val db = Firebase.firestore

        // Get Longitude and latitude 5m
        val coef = 10 / 111320.00
        val lat_max = coordinates.latitude + coef
        val lat_min = coordinates.latitude - coef
        val lon_max = coordinates.longitude + coef / cos(Math.PI/180)
        val lon_min = coordinates.longitude - coef / cos(Math.PI/180)

        Log.d("COORD", "($lat_max, $lon_min)")

        db.collection("capsule")
            .whereLessThanOrEqualTo("coord.latitude", lat_max)
            .whereGreaterThanOrEqualTo("coord.latitude", lat_min)
            .get()
            .addOnSuccessListener { snapshot ->
                // Filter on longitude locally
                val filteredDocuments = snapshot.documents.filter { document ->
                    val docLatitude = document.getDouble("coord.latitude") ?: 0.0
                    val docLongitude = document.getDouble("coord.longitude") ?: 0.0
                    docLongitude in lon_min..lon_max
                }

                Log.d("SUCCESS", filteredDocuments.size.toString())
            }
            .addOnFailureListener {
                Log.d("FAILURE", it.toString())
            }

    }
}