package yayuki.heartrate.android.user

import android.app.Application
import yayuki.heartrate.client.UserClient
import yayuki.heartrate.client.repo.BaseUserRepository

abstract class BaseApplication : Application() {
    abstract val client: UserClient
    abstract val userRepository: BaseUserRepository<*>
}