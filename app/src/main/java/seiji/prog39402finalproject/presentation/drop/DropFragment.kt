package seiji.prog39402finalproject.presentation.drop

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.GoogleApi
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import seiji.prog39402finalproject.R
import seiji.prog39402finalproject.data.remote.models.CapsuleRemoteModel
import seiji.prog39402finalproject.databinding.FragmentDropBinding
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.coroutines.CoroutineContext
import kotlin.system.exitProcess

class DropFragment : Fragment() {

    private val viewModel: DropViewModel by viewModels()

    /**
     * Current coordinates cannot be accessed when [Manifest.permission.ACCESS_FINE_LOCATION]
     * is not allowed. Therefore, prevent the app from performing Firestore writes
     */
    private var fineAccessAvailable = false

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        fineAccessAvailable = when {
            permissions.getOrDefault(
                Manifest.permission.ACCESS_FINE_LOCATION,
                false
            ) -> true
            else -> false
        }
    }

    @SuppressLint("MissingPermission")
    private val locationUpdateLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        LocationServices.getFusedLocationProviderClient(requireContext()).requestLocationUpdates(
            LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                10000
            )
                .setWaitForAccurateLocation(true)
                .build(),
            locationReceivedCallback,
            null
        )
    }

    private val locationReceivedCallback = object : LocationCallback() {
        override fun onLocationResult(lr: LocationResult) {
            super.onLocationResult(lr)
            lr.lastLocation?.let { loc ->
                viewModel.updateCapsule { capsule ->
                    capsule.copy(coord = GeoPoint(loc.latitude, loc.longitude))
                }
                Log.d("LOCATION", "${LatLng(loc.latitude, loc.longitude)}")
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ActivityCompat.checkSelfPermission(
                this@DropFragment.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@DropFragment.requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }



        LocationServices.getFusedLocationProviderClient(requireContext()).requestLocationUpdates(
            LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                10000
            )
                .setWaitForAccurateLocation(true)
                .build(),
            object : LocationCallback() {
                override fun onLocationResult(lr: LocationResult) {
                    super.onLocationResult(lr)
                    lr.lastLocation?.let { loc ->
                        Log.d("LOCATION", "${LatLng(loc.latitude, loc.longitude)}")
                    }
                }
            },
            Looper.getMainLooper()
        )
    }

    override fun onResume() {
        super.onResume()
        locationUpdateLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    override fun onPause() {
        super.onPause()
        LocationServices.getFusedLocationProviderClient(requireContext())
            .removeLocationUpdates(locationReceivedCallback)
    }


    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return FragmentDropBinding.inflate(inflater).apply {
            button.setOnClickListener {
                buttonInsertHandler(this)
            }
        }.root
    }

    private fun buttonInsertHandler(binding: FragmentDropBinding) {
        viewModel.updateCapsule {
            it.copy(
                title = binding.editTitle.text.toString(),
                body = binding.editBody.text.toString()
            )
        }
        viewModel.createCapsule(
            onSuccess = {
                findNavController().navigate(R.id.action_dropFragment_to_homeFragment)
            },
            onFailure = {
               // Display something or whatever
            }
        )
    }
}