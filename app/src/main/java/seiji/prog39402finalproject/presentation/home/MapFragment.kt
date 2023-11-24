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
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdate

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
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

class MapFragment : Fragment(), OnMarkerClickListener {

    val viewModel: HomeViewModel by viewModels()
    private lateinit var map: GoogleMap

    @SuppressLint("MissingPermission")
    private val locPollerWithPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        when {
            it.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
//                (requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager).let { man ->
//                    man.requestLocationUpdates(
//                        LocationManager.GPS_PROVIDER,
//                        0L,
//
//                        0f
//
//                    ) { loc ->
//                        val newLoc = LatLng(loc.latitude, loc.longitude)
//                        viewModel.updateLocation(newLoc)
//                    }
//                }
                LocationServices.getFusedLocationProviderClient(requireContext()).requestLocationUpdates(
                    LocationRequest.Builder(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        0
                    )
                        .setWaitForAccurateLocation(true)
                        .build(),
                    locationReceivedCallback,
                    Looper.getMainLooper()
                )
            }
        }
    }

    private val locationReceivedCallback = object : LocationCallback() {
        override fun onLocationResult(lr: LocationResult) {
            super.onLocationResult(lr)
            lr.lastLocation?.let { loc ->
                val newLoc = LatLng(loc.latitude, loc.longitude)

//                viewModel.currentLocation.value?.let { lastLoc ->
//                    if (lastLoc.getDistance(newLoc) >= 5E-5) return
//                }

                viewModel.updateLocation(newLoc)
                Log.d("LOCATION", "$newLoc")
            }
        }
    }

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        map = googleMap
        map.setOnMarkerClickListener(this)

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


            // Update nearby messages
            // Only request when new distance is 3m or farther from the previous requested coordinate

            viewModel.attemptGetNearbyCapsules(
                center = it,
                onSuccess = { caps ->
                    Log.d("NearbyCapsules", "Noice :D  :::  $caps")
                    // remove markers not present in the new array


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
                },
                onFailure = {
                    Log.d("NearbyCapsules", ":(")
                }
            )
        }
        // Place capsule markers
        // Fetch list of nearby capsules
        // Filter capsules not in the new list -> remove that marker... something like that


    }

    override fun onResume() {
        super.onResume()
        locPollerWithPermissions.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
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
        mapFragment?.getMapAsync(callback)
    }

    override fun onPause() {
        super.onPause()
        LocationServices.getFusedLocationProviderClient(requireContext())
            .removeLocationUpdates(locationReceivedCallback)
    }

    override fun onStop() {
        super.onStop()
        map.clear()
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        p0.remove()
        return true
    }
}