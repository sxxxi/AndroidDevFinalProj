package seiji.prog39402finalproject.data.remote.models

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint


data class CapsuleRemoteModel(
    val id: String,
    val title: String,
    val body: String,
    val geoHash: String,
    val coord: GeoPoint,
    val epochCreate: Long,
    val imageLinks: List<String>
) : FirestoreMap {
    override fun toFirestoreMap(): Map<String, Any> {
        return hashMapOf(
            "title" to title,
            "body" to body,
            "createEpoch" to epochCreate,
            "coord" to coord,
            "geoHash" to geoHash,
            "images" to imageLinks
        )
    }

    companion object {
        fun from(doc: DocumentSnapshot): CapsuleRemoteModel {
            return CapsuleRemoteModel(
                id = doc.id,
                title = doc.getString("title") ?: "",
                body = doc.getString("body") ?: "",
                geoHash = doc.getString("geoHash") ?: "",
                coord = doc.getGeoPoint("coord") ?: GeoPoint(0.0, 0.0),
                epochCreate = doc.getLong("createEpoch") ?: -1,
                imageLinks = (doc.get("images") ?: listOf<String>()) as List<String>
            )
        }
    }

}
