package yayuki.heartrate.android.user.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import yayuki.heartrate.android.user.R
import yayuki.heartrate.android.user.databinding.ActivityMainBinding
import yayuki.heartrate.client.data.ClientState

abstract class BaseActivity : AppCompatActivity() {
    abstract val viewModel: UserViewModel<*>
    private lateinit var binding: ActivityMainBinding
    private var isShowLogin = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.toolbar.title = viewModel.user.name
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        viewModel.clientState.observe(this) {
            if (it == ClientState.DISCONNECT && !isShowLogin) {
                isShowLogin = true
                val intent = Intent(this, LoginActivity::class.java)
                startActivityForResult(intent, 123)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 123) {
            if (resultCode == RESULT_OK) isShowLogin = false
            else {
                Toast.makeText(this, R.string.needLogin, Toast.LENGTH_SHORT).show()
            }
        } else super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        if (!supportFragmentManager.popBackStackImmediate()) {
            finishAffinity()
        }
    }
}