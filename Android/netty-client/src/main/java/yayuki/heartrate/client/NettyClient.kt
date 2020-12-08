package yayuki.heartrate.client

import com.google.protobuf.Message
import io.netty.bootstrap.Bootstrap
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withTimeoutOrNull
import yayuki.heartrate.MyResult
import yayuki.heartrate.client.data.ClientState
import yayuki.heartrate.client.error.ClientErrorCode
import yayuki.heartrate.netty.handler.CustomProtobufDecoder
import yayuki.heartrate.netty.handler.CustomProtobufEncoder
import kotlinx.coroutines.channels.Channel as KTChannel


open class NettyClient : Client {
    private val _state = MutableStateFlow(ClientState.DISCONNECT)
    override val state: StateFlow<ClientState>
        get() = _state.asStateFlow()

    override val isConnected: Boolean
        get() = channel?.isActive == true

    private var channel: Channel? = null

    private var connectChannel: KTChannel<MyResult<Unit>>? = null

    @Synchronized
    override suspend fun connect(hostname: String, port: Int): MyResult<Unit> {
        if (channel?.isActive == true) return MyResult.Success(Unit)
        println("start connect")
        _state.value = ClientState.CONNECTING
        val bootstrap = Bootstrap()
        val group = NioEventLoopGroup()
        bootstrap.group(group)
        bootstrap.channel(NioSocketChannel::class.java)
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 8000)
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
        bootstrap.handler(object : ChannelInitializer<SocketChannel>() {
            @Throws(Exception::class)
            override fun initChannel(ch: SocketChannel) {
                initPipeline(ch.pipeline())
            }
        })
        bootstrap.connect(hostname, port).addListener(connectListener)
            .channel().closeFuture().addListener(closeListener)
        connectChannel = KTChannel()
        return withTimeoutOrNull(9000) {
            val receive = connectChannel!!.receive()
            connectChannel!!.close()
            connectChannel = null
            receive
        } ?: MyResult.Error(ClientErrorCode.TIME_OUT)
    }

    protected open fun initPipeline(p: ChannelPipeline) {
        p.addLast(CustomProtobufDecoder())
        p.addLast(CustomProtobufEncoder())
    }

    override fun disconnect() {
        channel?.close()
    }

    override fun send(data: Message) {
        channel?.writeAndFlush(data)
    }

    private val connectListener: ChannelFutureListener = ChannelFutureListener {
        println("${Thread.currentThread().name} : connect:${it.isSuccess}")
        if (it.isSuccess) {
            channel = it.channel()
            _state.value = ClientState.CONNECTED
            connectChannel!!.sendBlocking(MyResult.Success(Unit))
        } else {
            connectChannel!!.sendBlocking(MyResult.Error(ClientErrorCode.TIME_OUT))
        }
    }

    private val closeListener: ChannelFutureListener = ChannelFutureListener {
        println("${Thread.currentThread().name} : close")
        channel = null
        _state.value = ClientState.DISCONNECT
    }
}