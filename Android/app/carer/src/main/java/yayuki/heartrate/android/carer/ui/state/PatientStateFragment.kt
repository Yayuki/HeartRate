package yayuki.heartrate.android.carer.ui.state

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import yayuki.heartrate.android.carer.CarerApplication
import yayuki.heartrate.android.carer.R
import yayuki.heartrate.android.carer.databinding.FragmentPatientBinding
import yayuki.heartrate.android.carer.ui.CarerViewModel
import yayuki.heartrate.carer.data.Patient

class PatientStateFragment : Fragment(), OnMapReadyCallback {
    private val viewModel: CarerViewModel by activityViewModels {
        val app = requireActivity().application as CarerApplication
        CarerViewModel.Creator(app.userRepository).getFactory()
    }
    private var _binding: FragmentPatientBinding? = null
    private val binding get() = _binding!!
    private lateinit var patient: Patient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPatientBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.focusPatient.observe(viewLifecycleOwner) {
            patient = it
            binding.include.name.text = it.user.name
            binding.include.hrValue.text = it.extra.hrValue.toString()
            val latLng = LatLng(patient.extra.latitude, patient.extra.longitude)
            marker?.position = latLng
            circle?.center = latLng
            circle?.radius = patient.extra.accuracy.toDouble()
        }
        viewModel.patientsState.observe(viewLifecycleOwner) {
            if (it.id != patient.user.id) return@observe
            binding.include.hrValue.text = it.hrValue.toString()
            map?.apply {
                val latLng = LatLng(it.latitude, it.longitude)
                animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f), 200, null)
                marker?.position = latLng
                circle?.center = latLng
                circle?.radius = it.accuracy.toDouble()
            }
        }
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        marker = null
        circle = null
    }

    private var map: GoogleMap? = null
    private var marker: Marker? = null
    private var circle: Circle? = null
    override fun onMapReady(gmap: GoogleMap?) {
        gmap?.apply {
            map = this
            val latLng = LatLng(24.195247625487852, 121.10418707075449)
            moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
            marker = addMarker(MarkerOptions().position(latLng))

            circle = addCircle(
                CircleOptions()
                    .center(latLng)
                    .radius(patient.extra.accuracy.toDouble())
                    .fillColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.map_circle_fill
                        )
                    )
                    .strokeColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.map_circle_stroke
                        )
                    )
                    .strokeWidth(5f)
            )
        }
    }
}