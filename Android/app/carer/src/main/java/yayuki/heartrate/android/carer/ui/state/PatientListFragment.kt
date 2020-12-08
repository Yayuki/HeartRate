package yayuki.heartrate.android.carer.ui.state

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.recyclerview.widget.DividerItemDecoration
import yayuki.heartrate.android.carer.CarerApplication
import yayuki.heartrate.android.carer.R
import yayuki.heartrate.android.carer.databinding.FragmentPatientListBinding
import yayuki.heartrate.android.carer.ui.CarerViewModel

class PatientListFragment : Fragment() {
    private val viewModel: CarerViewModel by activityViewModels {
        val app = requireActivity().application as CarerApplication
        CarerViewModel.Creator(app.userRepository).getFactory()
    }
    private var _binding: FragmentPatientListBinding? = null
    private val binding get() = _binding!!

    private val stateFragment = PatientStateFragment()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.e("PatientListFragment", "onCreateView")
        _binding = FragmentPatientListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("PatientListFragment", "onViewCreated")
        val patientAdapter = PatientAdapter(viewModel.extra.patientList) {
            viewModel.setFocus(it)
            if (!stateFragment.isAdded) {
                parentFragmentManager.beginTransaction()
                    .add(R.id.main_container, stateFragment)
                    .commit()
            }
            parentFragmentManager.commit {
                hide(this@PatientListFragment)
                show(stateFragment)
                addToBackStack("list")
            }
        }
        binding.list.apply {
            adapter = patientAdapter
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        }
        viewModel.patientsState.observe(viewLifecycleOwner) {
            patientAdapter.updateState(it.id)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e("PatientListFragment", "onDestroyView")
        _binding = null
    }
}