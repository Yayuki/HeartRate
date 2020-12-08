package yayuki.heartrate.android.patient.ui

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import yayuki.heartrate.android.user.ui.UserViewModel
import yayuki.heartrate.android.util.CustomVMFactory
import yayuki.heartrate.patient.data.PatientExtra
import yayuki.heartrate.patient.repo.PatientRepository

class PatientViewModel(
    private val userRepository: PatientRepository
) : UserViewModel<PatientExtra>(userRepository) {
    val hrValue get() = userRepository.hrValueFlow.asLiveData()

    fun connect() = viewModelScope.launch {
        userRepository.connect()
    }


    class Creator(
        private val userRepository: PatientRepository
    ) : CustomVMFactory.Creator(PatientViewModel::class.java) {
        override fun createViewModel(): PatientViewModel {
            return PatientViewModel(userRepository)
        }
    }
}