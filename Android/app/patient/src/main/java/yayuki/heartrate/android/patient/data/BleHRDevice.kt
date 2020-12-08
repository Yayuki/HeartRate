package yayuki.heartrate.android.patient.data

import android.bluetooth.*
import android.content.Context
import android.util.Log
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.runBlocking
import yayuki.heartrate.MyResult
import yayuki.heartrate.android.patient.BleScanner
import yayuki.heartrate.patient.data.HRDataSource
import java.util.*

@Suppress("SpellCheckingInspection")
class BleHRDevice(private val ctx: Context) : HRDataSource {
    companion object {
        const val DEVICE_NAME = "MTKBTDEVICE"
        val UUID_HR_SERVICE: UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")
        val UUID_HRM_CHARACTERISTIC: UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")
        val UUID_CCC_DESCRIPTOR: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }

    override var isConnect: Boolean = false
        private set
    private val scanner = BleScanner(ctx, DEVICE_NAME)

    private val _hrValueFlow = MutableSharedFlow<Int>(1, 1, BufferOverflow.DROP_OLDEST)
    override val hrValueFlow: SharedFlow<Int>
        get() = _hrValueFlow.asSharedFlow()

    override suspend fun connect() {
        _hrValueFlow.tryEmit(0)
        val myResult = scanner.scan()
        Log.e("BleHR", myResult.toString())
        when (myResult) {
            is MyResult.Success -> {
                myResult.data.connectGatt(ctx, true, gattCallback)
            }
            is MyResult.Error -> {
                isConnect = false
            }
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) gatt?.discoverServices()
            else isConnect = false
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) return
            gatt?.apply {
                val service = getService(UUID_HR_SERVICE)
                val characteristic = service.getCharacteristic(UUID_HRM_CHARACTERISTIC)
                val descriptor = characteristic.getDescriptor(UUID_CCC_DESCRIPTOR)
                setCharacteristicNotification(characteristic, true)
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                writeDescriptor(descriptor)
                isConnect = true
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            if (characteristic?.uuid?.equals(UUID_HRM_CHARACTERISTIC) == true) {
                val value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1);
                runBlocking {
                    _hrValueFlow.emit(value)
                }
            }
        }
    }
}