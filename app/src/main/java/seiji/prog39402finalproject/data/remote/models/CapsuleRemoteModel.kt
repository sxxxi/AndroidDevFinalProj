package seiji.prog39402finalproject.data.remote.models

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.getField
import java.time.LocalDateTime
import java.time.ZoneOffset


data class CapsuleRemoteModel(
    val id: String,
    val title: String,
    val body: String,
    val epochCreate: Long,
    val coord: GeoPoint
) : FirestoreMap {
    override fun toFirestoreMap(): Map<String, Any> {
        return hashMapOf(
            "title" to title,
            "body" to body,
            "epoch_created" to epochCreate,
            "coord" to coord
        )
    }

    companion object {
        fun create(
            id: String = "",
            title: String = "",
            body: String = "",
            coord: GeoPoint = GeoPoint(0.0, 0.0)
        ): CapsuleRemoteModel {
            return CapsuleRemoteModel(
                id = id,
                title = title,
                body = body,
                coord = coord,
                epochCreate = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
            )
        }

        fun from(doc: DocumentSnapshot): CapsuleRemoteModel {
            return CapsuleRemoteModel(
                id = doc.id,
                title = doc.getString("title") ?: "",
                body = doc.getString("body") ?: "",
                epochCreate = doc.getLong("create_date") ?: -1,
                coord = GeoPoint(doc.getDouble("coord.latitude") ?: 0.0, doc.getDouble("coord.longitude") ?: 0.0)
            )
        }
    }

}
