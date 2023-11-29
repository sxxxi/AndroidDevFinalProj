package seiji.prog39402finalproject.presentation.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.AdvancedMarkerOptions
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import seiji.prog39402finalproject.R
import seiji.prog39402finalproject.domain.models.Capsule

class MapFragment(
) : Fragment(), OnMarkerClickListener, OnMapReadyCallback {

    private lateinit var viewModel: HomeViewModel
    private lateinit var map: GoogleMap

    private var userMarker: Marker? = null
    private var capsuleMarkers: List<Marker?> = listOf()

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMarkerClickListener(this)
        map.uiSettings.isScrollGesturesEnabled = false

//        userMarker = googleMap.addMarker(
//            AdvancedMarkerOptions()
//                .position(ZERO_LAT_LNG)
//                .iconView(UserMarkerBinding.inflate(layoutInflater).root)
//        )

//        val b = ResourcesCompat.getDrawable(resources, R.drawable.baseline_search_24, null)?.toBitmap(200, 200)
//        b?.let { bitmap ->
//            userMarker?.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap))
//        }

        val circle = googleMap.addCircle(
            CircleOptions()
                .strokeWidth(PROXIMITY_CIRCLE_STROKE)
                .radius(PROXIMITY_RADIUS)
                .center(ZERO_LAT_LNG)
                .fillColor(Color(0, 0, 0, 50).toArgb())
        )

        // Place user marker
        viewModel.currentLocation.observe(viewLifecycleOwner) {
            userMarker?.position = it
            circle.center = it
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, CAMERA_ZOOM))
        }

        viewModel.nearbyCapsules.observe(viewLifecycleOwner) { caps ->
            // Remove past markers
            capsuleMarkers.forEach { mark ->
                mark?.remove()
            }
            // Replace and display :|
            capsuleMarkers = caps.map { map.createCapsuleMarker(it) }
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val id = marker.title

        viewModel.nearbyCapsules.value?.let {
            val selected = try {
                it.first { cap -> cap.id == id }
            } catch (e: NoSuchElementException) {
                Log.e(TAG, "HELLO THERE!")
                null
            }
            viewModel.setFocusedCapsule(selected)

        }

        return true
    }

    private fun GoogleMap.createCapsuleMarker(capsule: Capsule): Marker? {
        return ResourcesCompat.getDrawable(resources, R.drawable.msg_bubble, null)
            ?.toBitmap(100, 100)?.let { icon ->
            addMarker(
                AdvancedMarkerOptions()
                    .title(capsule.id)
                    .position(capsule.coord)
                    .icon(BitmapDescriptorFactory.fromBitmap(icon))
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onStart() {
        super.onStart()
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            while (isActive) {
                val state = viewLifecycleOwner.lifecycle.currentState
                if (state == Lifecycle.State.RESUMED) {
                    viewModel.currentLocation.value?.let { loc ->
                        delay(NEARBY_CAPSULE_REQUEST_INTERVAL)
                        Log.d("GREETER", "Hey :)")
                        viewModel.attemptGetNearbyCapsules(loc, PROXIMITY_RADIUS)
                    }
                }
            }
        }
    }


    companion object {
        private const val TAG = "MapFragment"
        private val ZERO_LAT_LNG = LatLng(0.0, 0.0)
        private const val CAMERA_ZOOM = 100f
        private const val PROXIMITY_CIRCLE_STROKE = 4f
        const val PROXIMITY_RADIUS = 5.0
        private const val NEARBY_CAPSULE_REQUEST_INTERVAL = 3000L
    }
}