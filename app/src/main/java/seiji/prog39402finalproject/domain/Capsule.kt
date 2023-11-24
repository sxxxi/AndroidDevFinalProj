package seiji.prog39402finalproject.domain

import com.google.android.gms.maps.model.LatLng


data class Capsule(
    val id: String,
    val title: String,
    val body: String,
    val epochCreate: Long,
    val coord: LatLng
)
