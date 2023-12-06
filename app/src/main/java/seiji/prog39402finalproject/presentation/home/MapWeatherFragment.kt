package seiji.prog39402finalproject.presentation.home

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import seiji.prog39402finalproject.R
import seiji.prog39402finalproject.databinding.FragmentMapWeatherBinding

class MapWeatherFragment : Fragment() {

    private lateinit var vm: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(requireActivity())[WeatherViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return FragmentMapWeatherBinding.inflate(inflater).apply {
            vm.currentWeather.observe(viewLifecycleOwner) { weather ->
                val (header, description) = if (weather != null) {
                   weather.main to weather.description
                } else {
                    "-" to "-"
                }
                weatherHeader.text = header
                weatherDescription.text = description
            }
        }.root
    }

    companion object {
        private const val LOCATION_REQUEST_INTERVAL = 300000L
    }
}