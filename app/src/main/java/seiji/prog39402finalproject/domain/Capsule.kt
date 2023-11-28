package seiji.prog39402finalproject.domain

import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import java.time.LocalDateTime
import java.time.ZoneOffset


data class Capsule(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val epochCreate: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
    val coord: LatLng = LatLng(0.0, 0.0),
    val geoHash: String = "",
    val images: List<String> = listOf()
)
