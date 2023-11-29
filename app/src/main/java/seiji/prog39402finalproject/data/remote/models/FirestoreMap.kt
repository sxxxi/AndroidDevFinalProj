package seiji.prog39402finalproject.data.remote.models

interface FirestoreMap {
    fun toFirestoreMap(): Map<String, Any>
}