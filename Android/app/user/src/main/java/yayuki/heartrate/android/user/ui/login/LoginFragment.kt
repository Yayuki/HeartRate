package yayuki.heartrate.android.user.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import yayuki.heartrate.MyResult
import yayuki.heartrate.android.user.BaseApplication
import yayuki.heartrate.android.user.R
import yayuki.heartrate.android.user.databinding.FragmentLoginBinding
import yayuki.heartrate.android.user.ui.LoginActivity
import yayuki.heartrate.client.data.User

class LoginFragment : Fragment() {
    private val loginViewModel: LoginViewModel by activityViewModels {
        val app = requireActivity().application as BaseApplication
        LoginViewModel.Creator(app.userRepository).getFactory()
    }
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginViewModel.setHostNamePort("heartrate.yayuki.ml", 10600)
        loginViewModel.loginFormState.observe(viewLifecycleOwner) { it ->
            binding.login.isEnabled = it.isDataValid
            it.usernameError?.let {
                binding.username.error = getString(it)
            }
            it.passwordError?.let {
                binding.password.error = getString(it)
            }
        }

        loginViewModel.loginResult.observe(viewLifecycleOwner) {
            binding.username.isEnabled = true
            binding.password.isEnabled = true
            binding.login.isEnabled = true
            binding.loading.visibility = View.GONE
            when (it) {
                is MyResult.Success -> {
                    updateUiWithUser(it.data)
                    (requireActivity() as? LoginActivity)?.startMain()
                }
                is MyResult.Error -> {

                }
            }
        }

        binding.username.addTextChangedListener(afterTextChangedListener)
        binding.password.addTextChangedListener(afterTextChangedListener)
        binding.password.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.login.performClick()
            }
            false
        }

        binding.login.setOnClickListener {
            if (!binding.login.isEnabled) return@setOnClickListener
            binding.username.isEnabled = false
            binding.password.isEnabled = false
            binding.login.isEnabled = false
            binding.loading.visibility = View.VISIBLE
            loginViewModel.login(
                binding.username.text.toString(),
                binding.password.text.toString()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e("LoginFragment", "onDestroyView")
        _binding = null
    }

    private fun updateUiWithUser(user: User) {
        val welcome = getString(R.string.welcome) + user.name
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    private val afterTextChangedListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(s: Editable) {
            loginViewModel.loginDataChanged(
                binding.username.text.toString(),
                binding.password.text.toString()
            )
        }
    }
}