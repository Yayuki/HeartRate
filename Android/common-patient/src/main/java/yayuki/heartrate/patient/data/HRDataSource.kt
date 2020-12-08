package yayuki.heartrate.patient.data

import kotlinx.coroutines.flow.SharedFlow

interface HRDataSource {
    val isConnect: Boolean
    val hrValueFlow: SharedFlow<Int>
    suspend fun connect()
}