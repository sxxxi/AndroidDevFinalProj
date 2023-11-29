package seiji.prog39402finalproject.presentation.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import seiji.prog39402finalproject.databinding.FragmentInspectBinding
import seiji.prog39402finalproject.domain.adapters.ImagePagerAdapter


class InspectFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentInspectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentInspectBinding.inflate(inflater, container, false).apply {
            viewModel.selectedCapsule.observe(viewLifecycleOwner) { capsule ->
                capsule?.let { cap ->
                    viewModel.getCapsuleImages(cap) { images ->
                        Log.d("Am i called?", "HERE")
                        vpImages.adapter = ImagePagerAdapter(images)
                    }
                }
                updateVisibility()
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        updateVisibility()
    }

    private fun updateVisibility() {
        binding.apply {
            val capsule = viewModel.selectedCapsule.value
            root.visibility = capsule?.let {View.VISIBLE} ?: View.INVISIBLE

            if (capsule != null) {
                textFocusTitle.text = capsule.title
                textFocusBody.text = capsule.body
            } else {
                vpImages.adapter = null
            }
        }
    }

    fun setOnCloseClicked(onClick: () -> Unit) {
        binding.buttonFocusClose.setOnClickListener {
            onClick()
        }
    }
}