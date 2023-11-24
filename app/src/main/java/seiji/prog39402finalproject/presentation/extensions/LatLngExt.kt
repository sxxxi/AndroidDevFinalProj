package seiji.prog39402finalproject.presentation.extensions

import com.google.android.gms.maps.model.LatLng
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Pythagorean time!
 */
fun LatLng.getDistance(p1: LatLng): Double {
    val lat = latitude - p1.latitude
    val lon = longitude - p1.longitude

    return abs(sqrt(lat.pow(2.0) + lon.pow(2.0)))
}
