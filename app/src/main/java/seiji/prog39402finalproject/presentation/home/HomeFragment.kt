package seiji.prog39402finalproject.presentation.home

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import seiji.prog39402finalproject.R
import seiji.prog39402finalproject.databinding.FragmentHomeBinding
import seiji.prog39402finalproject.domain.adapters.FragmentPagerAdapter

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var foo: InspectFragment

    @SuppressLint("MissingPermission")
    private val locPollerWithPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        when {
            it.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                LocationServices.getFusedLocationProviderClient(requireContext())
                    .requestLocationUpdates(
                        LocationRequest.Builder(
                            Priority.PRIORITY_HIGH_ACCURACY,
                            LOCATION_REQUEST_INTERVAL
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
                viewModel.updateLocation(newLoc)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        weatherViewModel  = ViewModelProvider(requireActivity())[WeatherViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return FragmentHomeBinding.inflate(inflater).apply {
            fragPager.adapter = FragmentPagerAdapter(
                parentFragmentManager,
                lifecycle,
                arrayOf(
                    MapWeatherFragment(),
//                    MapFragment(),
                    CapsuleListFragment()
                )
            )

            buttonDrop.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_dropFragment)
            }

            parentFragmentManager.commit {
                foo = InspectFragment()
                setReorderingAllowed(true)
                replace(R.id.inspected_capsule, foo, "BOBBY")

                runOnCommit {
                    foo.setOnCloseClicked {
                        viewModel.setFocusedCapsule(null)
                    }
                }
            }
        }.root
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch(Dispatchers.Default) {
            while (isActive) {
                if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                    delay(5000)
                    viewModel.currentLocation.value?.let { currentLoc ->
                        weatherViewModel.getWeather(currentLoc)
                    }
                    delay(300000)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        locPollerWithPermissions.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    override fun onPause() {
        super.onPause()
        LocationServices.getFusedLocationProviderClient(requireContext())
            .removeLocationUpdates(locationReceivedCallback)
    }

    companion object {
        const val LOCATION_REQUEST_INTERVAL = 0L
    }
}