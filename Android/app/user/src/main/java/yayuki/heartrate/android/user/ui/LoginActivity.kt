package yayuki.heartrate.android.user.ui

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.commitNow
import yayuki.heartrate.android.user.BaseApplication
import yayuki.heartrate.android.user.R
import yayuki.heartrate.android.user.ui.login.LoginFragment
import yayuki.heartrate.android.user.ui.login.LoginViewModel
import yayuki.heartrate.client.data.ClientState

class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels {
        val app = application as BaseApplication
        LoginViewModel.Creator(app.userRepository).getFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (loginViewModel.clientState.value == ClientState.CONNECTED &&
            loginViewModel.isLogin
        ) {
            startMain()
        } else {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
            findViewById<Toolbar>(R.id.toolbar).title = "Login"
            supportFragmentManager.commitNow {
                replace(R.id.main_container, LoginFragment())
            }
        }

    }

    fun startMain() {
        val intent = Intent()
        intent.component = ComponentName(
            applicationContext, "${applicationContext.packageName}.MainActivity"
        )
        setResult(RESULT_OK)
        startActivity(intent)
    }
}