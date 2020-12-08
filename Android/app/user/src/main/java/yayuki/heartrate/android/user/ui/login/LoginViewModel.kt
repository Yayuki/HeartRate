package yayuki.heartrate.android.user.ui.login

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import yayuki.heartrate.MyResult
import yayuki.heartrate.android.user.R
import yayuki.heartrate.android.util.CustomVMFactory
import yayuki.heartrate.client.data.User
import yayuki.heartrate.client.repo.BaseUserRepository

class LoginViewModel(
    private val userRepository: BaseUserRepository<*>
) : ViewModel() {
    val clientState get() = userRepository.clientState
    val isLogin get() = userRepository.isLogin

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<MyResult<User>>()
    val loginResult: LiveData<MyResult<User>> = _loginResult

    fun setHostNamePort(hostname: String, port: Int) =
        userRepository.setHostNamePort(hostname, port)

    fun login(account: String, password: String) = viewModelScope.launch {
        _loginResult.value = userRepository.login(account, password)
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    private fun isUserNameValid(username: String): Boolean {
        return username.isNotBlank()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 2
    }

    class Creator(
        private val userRepository: BaseUserRepository<*>
    ) : CustomVMFactory.Creator(LoginViewModel::class.java) {
        override fun createViewModel(): LoginViewModel {
            return LoginViewModel(userRepository)
        }
    }
}