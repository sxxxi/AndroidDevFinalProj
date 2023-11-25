package seiji.prog39402finalproject.presentation.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import seiji.prog39402finalproject.databinding.FragmetCapsuleListBinding
import seiji.prog39402finalproject.domain.Capsule
import seiji.prog39402finalproject.domain.adapters.CapsulePreviewAdapter

class CapsuleListFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private var nearbyCapsules: List<Capsule> = listOf(
        Capsule(
            id = "",
            title = "HI",
            body = "BODY",
            coord = LatLng(0.0, 0.0),
            epochCreate = System.currentTimeMillis()
        )
    )

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
                recyclerListCapsules.adapter = CapsulePreviewAdapter(nearbyCapsules)
                recyclerListCapsules.layoutManager = LinearLayoutManager(requireContext())
            }
        }.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.currentLocation.observe(viewLifecycleOwner) {
            Log.d("INSEPC", "$it")
        }
    }
}