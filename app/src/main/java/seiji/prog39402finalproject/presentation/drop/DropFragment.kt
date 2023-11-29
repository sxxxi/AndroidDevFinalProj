package seiji.prog39402finalproject.presentation.drop

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import seiji.prog39402finalproject.R
import seiji.prog39402finalproject.databinding.FragmentDropBinding
import seiji.prog39402finalproject.domain.adapters.CapsuleDropImagePreviewAdapter

class DropFragment : Fragment() {

    private lateinit var viewModel: DropViewModel
    private lateinit var binding: FragmentDropBinding

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
    private val locationUpdateLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            LocationServices.getFusedLocationProviderClient(requireContext())
                .requestLocationUpdates(
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
                viewModel.updateCapsulePos(LatLng(loc.latitude, loc.longitude))
            }
        }
    }

    private fun onAttachmentClicked(clicked: Bitmap) {
        viewModel.dequeueImage(clicked)
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

    private fun buttonAddImageHandler(binding: FragmentDropBinding) {
        findNavController().navigate(R.id.action_dropFragment_to_cameraFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[DropViewModel::class.java]

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

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return FragmentDropBinding.inflate(inflater).apply {
            binding = this

            viewModel.newCapsule.value?.let { capsule ->
                editTitle.setText(capsule.title)
                editBody.setText(capsule.body)
            }

            button.setOnClickListener {
                buttonInsertHandler(this)
            }

            buttonAddImage.setOnClickListener {
                buttonAddImageHandler(this)
            }

        }.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.capsuleImages.observe(viewLifecycleOwner) { images ->
            binding.apply {
                imagePreview.adapter = CapsuleDropImagePreviewAdapter(images, ::onAttachmentClicked)
                imagePreview.layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        locationUpdateLauncher.launch(
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
        private const val TAG = "DropFragment"
    }
}