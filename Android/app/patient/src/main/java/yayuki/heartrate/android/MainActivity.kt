package yayuki.heartrate.android

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.commitNow
import yayuki.heartrate.android.patient.PatientApplication
import yayuki.heartrate.android.patient.R
import yayuki.heartrate.android.patient.ui.PatientViewModel
import yayuki.heartrate.android.patient.ui.StateFragment
import yayuki.heartrate.android.user.ui.BaseActivity

class MainActivity : BaseActivity() {
    override val viewModel by viewModels<PatientViewModel> {
        val app = application as PatientApplication
        PatientViewModel.Creator(app.userRepository).getFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.commitNow {
            replace(R.id.main_container, StateFragment())
        }
    }
}