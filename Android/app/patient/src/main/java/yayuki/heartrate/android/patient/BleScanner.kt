package yayuki.heartrate.android.patient

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.withTimeoutOrNull
import yayuki.heartrate.MyResult


class BleScanner(
    private val ctx: Context,
    var targetName: String
) {
    private val manager = ctx.getSystemService(Context.BLUETOOTH_SERVICE)
            as BluetoothManager
    lateinit var deviceChannel: Channel<BluetoothDevice>

    suspend fun scan(): MyResult<BluetoothDevice> {
        deviceChannel = Channel()
        val adapter = manager.adapter
        for (bondedDevice in adapter.bondedDevices) {
            if (check(bondedDevice))
                return MyResult.Success(bondedDevice)
        }

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        ctx.registerReceiver(receiver, filter)
        return withTimeoutOrNull(5000) {
            val device = deviceChannel.receive()
            MyResult.Success(device)
        } ?: MyResult.Error(BleErrorCode.NOT_FOUND)
    }

    fun check(device: BluetoothDevice): Boolean {
        return targetName == device.name
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    if (!intent.hasExtra(BluetoothDevice.EXTRA_DEVICE)) return
                    val device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    if (check(device)) {
                        deviceChannel.sendBlocking(device)
                    }
                }
            }
        }
    }
}