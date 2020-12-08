package yayuki.heartrate.client

import com.google.protobuf.Message
import kotlinx.coroutines.flow.StateFlow
import yayuki.heartrate.client.data.ClientState

interface Client {
    val state: StateFlow<ClientState>
    val isConnected: Boolean
    suspend fun connect(hostname: String, port: Int): yayuki.heartrate.MyResult<Unit>
    fun disconnect()
    fun send(data: Message)
}