package seiji.prog39402finalproject.presentation.camera

import android.Manifest
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import seiji.prog39402finalproject.databinding.FragmentCameraBinding
import seiji.prog39402finalproject.presentation.drop.DropViewModel
import java.io.ByteArrayOutputStream
import java.io.File

class CameraFragment : Fragment() {

    private val vm: CameraViewModel by viewModels()
    private lateinit var dvm: DropViewModel
    private lateinit var previewCache: File

    private lateinit var binding: FragmentCameraBinding
    private val permissionChecker =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissisons ->
            var grant = true
            permissisons.forEach { permission ->
                grant = grant && (permission.key in REQUIRED_PERMISSIONS)
            }

            if (!grant) return@registerForActivityResult

            startCamera()
        }

    private fun initialize() {
        permissionChecker.launch(REQUIRED_PERMISSIONS)
    }

    private fun startCamera() {
        val processCameraFuture = ProcessCameraProvider.getInstance(requireContext())

        processCameraFuture.addListener({
            val cameraProvider = processCameraFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.preview.surfaceProvider)
            }
            // Default camera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind previous use cases
                cameraProvider.unbindAll()

                // Bind preview use case
                cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview)

            } catch (ex: Exception) {
                Log.e(TAG, "Use case binding failed", ex)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun createCache() {
        previewCache = File.createTempFile(CAPTURE_CACHE, null, requireContext().cacheDir)
    }

    private fun removeCache() {
        if (previewCache.exists()) previewCache.delete()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dvm = ViewModelProvider(requireActivity())[DropViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return FragmentCameraBinding.inflate(inflater).apply {
            binding = this

            vm.image.observe(viewLifecycleOwner) { bitmap ->
                // render preview and capture button if null

                // Show accept button, reject button and image view otherwise
                if (bitmap != null) {
                    confirmButtons.visibility = View.VISIBLE
                    imageConfirm.visibility = View.VISIBLE
                    imageConfirm.setImageBitmap(bitmap)
                    captureButtons.visibility = View.INVISIBLE

                } else {
                    confirmButtons.visibility = View.INVISIBLE
                    imageConfirm.visibility = View.INVISIBLE
                    captureButtons.visibility = View.VISIBLE
                }

            }

            buttonCapture.setOnClickListener {
                preview.bitmap?.let { bitmap ->
                    vm.capture(bitmap)
                    if (!previewCache.exists()) createCache()
                    previewCache
                        .outputStream().use { out ->
                            val bytes = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes)
                            out.write(bytes.toByteArray())
                        }
                }
            }

            buttonAccept.setOnClickListener {
                vm.image.value?.let { image ->
                    dvm.queueImage(image)
                    vm.reject()
                    findNavController().popBackStack()
                }
            }

            buttonReject.setOnClickListener {
                vm.reject()
            }
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    override fun onStart() {
        super.onStart()
        createCache()
    }

    override fun onStop() {
        super.onStop()
        removeCache()
    }


    companion object {
        private const val TAG = "CameraFragment"
        private const val CAPTURE_CACHE = "capture_cache"
        private val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        ).toTypedArray()
    }
}