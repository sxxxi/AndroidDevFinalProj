package seiji.prog39402finalproject.presentation.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import seiji.prog39402finalproject.R
import seiji.prog39402finalproject.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return FragmentHomeBinding.inflate(inflater).apply {
            buttonDrop.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_dropFragment)
            }
        }.root
    }
}