package yayuki.heartrate.client.repo

import yayuki.heartrate.MyResult
import yayuki.heartrate.client.UserClient
import yayuki.heartrate.client.data.Extra
import yayuki.heartrate.client.data.User
import yayuki.heartrate.error.LoginErrorCode

@Suppress("PropertyName")
abstract class BaseUserRepository<E : Extra>(
    private val clazz: Class<E>,
    private val client: UserClient
) {
    val clientState get() = client.state
    private var _isLogin = false
    val isLogin get() = _isLogin

    private var hostname: String = ""
    private var port: Int = 0

    private var _user = User(-1, "NotLogin")
    val user get() = _user
    protected abstract var _extra: E
    val extra get() = _extra

    fun setHostNamePort(hostname: String, port: Int) {
        this.hostname = hostname
        this.port = port
    }

    @Synchronized
    @Suppress("UNCHECKED_CAST")
    suspend fun login(account: String, password: String): MyResult<User> {
        _isLogin = false
        val connectResult = client.connect(hostname, port)
        if (connectResult is MyResult.Error) return connectResult
        return when (val userExtraResult = client.login(account, password)) {
            is MyResult.Success -> {
                return if (clazz.isInstance(userExtraResult.data.extra)) {
                    _user = userExtraResult.data.user
                    _extra = userExtraResult.data.extra as E
                    _isLogin = true
                    MyResult.Success(_user)
                } else {
                    MyResult.Error(LoginErrorCode.EXTRA_TYPE_ERROR)
                }
            }
            is MyResult.Error -> userExtraResult
        }
    }
}