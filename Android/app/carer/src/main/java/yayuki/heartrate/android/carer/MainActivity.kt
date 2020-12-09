package yayuki.heartrate.android.carer

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.commitNow
import yayuki.heartrate.android.carer.CarerApplication
import yayuki.heartrate.android.carer.R
import yayuki.heartrate.android.carer.ui.CarerViewModel
import yayuki.heartrate.android.carer.ui.state.PatientListFragment
import yayuki.heartrate.android.user.ui.BaseActivity

class MainActivity : BaseActivity() {
    override val viewModel by viewModels<CarerViewModel> {
        val app = application as CarerApplication
        CarerViewModel.Creator(app.userRepository).getFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.commitNow {
            replace(R.id.main_container, PatientListFragment())
        }
    }
}