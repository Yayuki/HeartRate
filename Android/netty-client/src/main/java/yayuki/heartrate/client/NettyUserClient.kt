package yayuki.heartrate.client

import io.netty.channel.ChannelPipeline
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withTimeoutOrNull
import yayuki.heartrate.MyResult
import yayuki.heartrate.client.data.UserExtra
import yayuki.heartrate.client.handler.LoginHandler
import yayuki.heartrate.error.LoginErrorCode
import yayuki.heartrate.net.protobuf.LoginProto

open class NettyUserClient(
    private val status: LoginProto.Request.Status,
    decoder: LoginHandler.ExtraDecoder
) : NettyClient(), UserClient {
    private val loginHandler = LoginHandler(decoder)
    private var loginChannel: Channel<MyResult<UserExtra>>? = null

    override fun initPipeline(p: ChannelPipeline) {
        super.initPipeline(p)
        p.addLast(loginHandler)
    }

    override suspend fun login(account: String, password: String): MyResult<UserExtra> {
        return withTimeoutOrNull(5000) {
            send(createLoginRequest(account, password))
            loginChannel = Channel()
            loginHandler.loginChannel = loginChannel
            loginChannel!!.receive()
        } ?: MyResult.Error(LoginErrorCode.TIME_OUT)
    }

    private fun createLoginRequest(account: String, password: String): LoginProto.Request {
        return LoginProto.Request.newBuilder()
            .setStatus(status)
            .setCredentials(createCredentials(account, password))
            .build()
    }

    private fun createCredentials(account: String, password: String): LoginProto.Credentials? {
        return LoginProto.Credentials.newBuilder()
            .setAccount(account)
            .setPassword(password)
            .build()
    }

    override fun logout() {

    }
}