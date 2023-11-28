package seiji.prog39402finalproject.presentation.home

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import seiji.prog39402finalproject.R
import seiji.prog39402finalproject.databinding.UserMarkerBinding

class MapFragment(
) : Fragment(), OnMarkerClickListener, OnMapReadyCallback {

    private lateinit var viewModel: HomeViewModel
    private lateinit var map: GoogleMap

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMarkerClickListener(this)
        map.uiSettings.isScrollGesturesEnabled = false

        var capsuleMarkers = listOf<Marker?>()

        val userMarker = googleMap.addMarker(
            AdvancedMarkerOptions()
                .position(LatLng(0.0, 0.0))
                .iconView(UserMarkerBinding.inflate(layoutInflater).root)
        )
        val circle = googleMap.addCircle(
            CircleOptions()
                .strokeWidth(2f)
                .radius(5.0)
                .center(LatLng(0.0, 0.0))
        )

        // Place user marker
        viewModel.currentLocation.observe(viewLifecycleOwner) {
            userMarker?.position = it
            circle.center = it
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 100f))
            viewModel.attemptGetNearbyCapsules(it)
        }

        viewModel.nearbyCapsules.observe(viewLifecycleOwner) { caps ->
            // Remove past markers
            capsuleMarkers.forEach { mark ->
                mark?.remove()
            }
            // Replace and display :|
            capsuleMarkers = caps.map { cap ->
                googleMap.addMarker(
                    AdvancedMarkerOptions()
                        .position(cap.coord)
                        .icon(BitmapDescriptorFactory.defaultMarker(50f))
                        .title(cap.id)

                )
            }
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

    override fun onStop() {
        super.onStop()
        map.clear()
    }

    companion object {
        private const val TAG = "MapFragment"
    }
}