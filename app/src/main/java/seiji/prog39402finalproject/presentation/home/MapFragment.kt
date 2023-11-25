package seiji.prog39402finalproject.presentation.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings.ZoomDensity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdate

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.AdvancedMarker
import com.google.android.gms.maps.model.AdvancedMarkerOptions
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions
import com.google.firebase.firestore.GeoPoint
import seiji.prog39402finalproject.R
import seiji.prog39402finalproject.databinding.UserMarkerBinding
import seiji.prog39402finalproject.presentation.extensions.getDistance

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
                        .title(cap.title)

                )
            }
        }
    }

    override fun onMarkerClick(p0: Marker): Boolean {
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
}