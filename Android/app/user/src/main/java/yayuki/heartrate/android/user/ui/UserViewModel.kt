package yayuki.heartrate.android.user.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import yayuki.heartrate.client.data.Extra
import yayuki.heartrate.client.repo.BaseUserRepository

open class UserViewModel<E : Extra>(
    private val userRepository: BaseUserRepository<E>
) : ViewModel() {
    val clientState get() = userRepository.clientState.asLiveData()
    val isLogin get() = userRepository.isLogin
    val user get() = userRepository.user
    val extra get() = userRepository.extra

    fun setHostNamePort(hostname: String, port: Int) =
        userRepository.setHostNamePort(hostname, port)

    fun login(account: String, password: String) = viewModelScope.launch {
        userRepository.login(account, password)
    }
}