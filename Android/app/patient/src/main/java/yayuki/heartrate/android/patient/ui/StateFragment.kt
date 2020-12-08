package yayuki.heartrate.android.patient.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import yayuki.heartrate.android.patient.PatientApplication
import yayuki.heartrate.android.patient.PermissionUtils
import yayuki.heartrate.android.patient.PermissionUtils.requestLocationPermissions
import yayuki.heartrate.android.patient.R
import yayuki.heartrate.android.patient.databinding.FragmentPatientBinding

class StateFragment : Fragment(R.layout.fragment_patient) {
    private val viewModel: PatientViewModel by activityViewModels {
        val app = requireActivity().application as PatientApplication
        PatientViewModel.Creator(app.userRepository).getFactory()
    }
    private var _binding: FragmentPatientBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentPatientBinding.bind(view)
        binding.carerName.text = viewModel.extra.carerName
        binding.carerPhone.text = viewModel.extra.carerPhone
        viewModel.hrValue.observe(viewLifecycleOwner) {
            binding.hrValue.text = it.toString()
        }
        if (requestLocationPermissions(123)) {
            viewModel.connect()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (PermissionUtils.checkResult(grantResults)) {
            viewModel.connect()
        } else {
            Toast.makeText(requireContext(), R.string.permission, Toast.LENGTH_SHORT).show()
        }
    }
}