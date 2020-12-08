package yayuki.heartrate.client.carer

import io.netty.channel.ChannelPipeline
import kotlinx.coroutines.flow.asSharedFlow
import yayuki.heartrate.client.CarerClient
import yayuki.heartrate.client.NettyUserClient
import yayuki.heartrate.client.carer.handler.StateHandler
import yayuki.heartrate.net.protobuf.LoginProto

class NettyCarerClient : NettyUserClient(
    LoginProto.Request.Status.CARER,
    CarerExtraDecoder()
), CarerClient {
    private val stateHandler = StateHandler()
    override val stateFlow get() = stateHandler.stateFlow.asSharedFlow()

    override fun initPipeline(p: ChannelPipeline) {
        super.initPipeline(p)
        p.addLast(stateHandler)
    }
}