package seiji.prog39402finalproject.data.remote.models

import com.google.firebase.firestore.DocumentReference

interface FirestoreMap {
    fun toFirestoreMap() : Map<String, Any>
}