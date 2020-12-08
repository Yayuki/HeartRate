package yayuki.heartrate.client.handler

import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import yayuki.heartrate.MyResult
import yayuki.heartrate.client.data.Extra
import yayuki.heartrate.client.data.User
import yayuki.heartrate.client.data.UserExtra
import yayuki.heartrate.error.LoginErrorCode
import yayuki.heartrate.net.protobuf.LoginProto

@ChannelHandler.Sharable
class LoginHandler(
    private val decoder: ExtraDecoder
) : SimpleChannelInboundHandler<LoginProto.Result>() {
    internal var loginChannel: Channel<MyResult<UserExtra>>? = null

    override fun channelRead0(ctx: ChannelHandlerContext, msg: LoginProto.Result) {
        loginChannel?.run {
            val myResult = if (msg.retCode == -1) {
                val user = User(msg.user.id, msg.user.name)
                val extra = decoder.map(msg)
                MyResult.Success(UserExtra(user, extra))
            } else {
                MyResult.Error(LoginErrorCode.getException(msg.retCode))
            }
            sendBlocking(myResult)
        }
    }

    interface ExtraDecoder {
        fun map(result: LoginProto.Result): Extra
    }
}