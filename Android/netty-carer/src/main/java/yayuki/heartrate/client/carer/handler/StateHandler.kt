package yayuki.heartrate.client.carer.handler

import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import yayuki.heartrate.carer.data.PatientStateEvent
import yayuki.heartrate.net.protobuf.PatientProto

@ChannelHandler.Sharable
class StateHandler : SimpleChannelInboundHandler<PatientProto.State>() {
    val stateFlow = MutableSharedFlow<PatientStateEvent>(10, 5, BufferOverflow.DROP_OLDEST)
    override fun channelRead0(ctx: ChannelHandlerContext?, msg: PatientProto.State) = runBlocking {
        stateFlow.emit(msg.run {
            PatientStateEvent(id, heartRate, accuracy, latitude, longitude)
        })
    }
}