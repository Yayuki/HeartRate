package yayuki.heartrate.client

import yayuki.heartrate.MyResult
import yayuki.heartrate.client.data.UserExtra

interface UserClient : Client {
    suspend fun login(account: String, password: String): MyResult<UserExtra>
    fun logout()
}