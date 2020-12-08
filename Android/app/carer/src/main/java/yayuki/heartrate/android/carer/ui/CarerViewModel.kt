package yayuki.heartrate.android.carer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import yayuki.heartrate.android.user.ui.UserViewModel
import yayuki.heartrate.android.util.CustomVMFactory
import yayuki.heartrate.carer.data.CarerExtra
import yayuki.heartrate.carer.data.Patient
import yayuki.heartrate.carer.repo.CarerRepository

class CarerViewModel(
    private val userRepository: CarerRepository
) : UserViewModel<CarerExtra>(
    userRepository
) {
    val patientsState get() = userRepository.stateFlow.asLiveData()

    private val _focusPatient = MutableLiveData<Patient>()
    val focusPatient: LiveData<Patient> = _focusPatient

    fun setFocus(patient: Patient) {
        _focusPatient.postValue(patient)
    }

    class Creator(
        private val userRepository: CarerRepository
    ) : CustomVMFactory.Creator(CarerViewModel::class.java) {
        override fun createViewModel(): CarerViewModel {
            return CarerViewModel(userRepository)
        }
    }
}