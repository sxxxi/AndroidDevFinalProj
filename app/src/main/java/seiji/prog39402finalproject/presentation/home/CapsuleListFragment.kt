package seiji.prog39402finalproject.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import seiji.prog39402finalproject.databinding.FragmetCapsuleListBinding
import seiji.prog39402finalproject.domain.adapters.CapsulePreviewAdapter
import seiji.prog39402finalproject.domain.models.Capsule

class CapsuleListFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private var nearbyCapsules: List<Capsule> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmetCapsuleListBinding.inflate(inflater).apply {
            viewModel.nearbyCapsules.observe(viewLifecycleOwner) { capsules ->
                nearbyCapsules = capsules
                recyclerListCapsules.adapter =
                    CapsulePreviewAdapter(nearbyCapsules, viewModel::setFocusedCapsule)
                recyclerListCapsules.layoutManager = LinearLayoutManager(requireContext())
            }
        }.root
    }
}