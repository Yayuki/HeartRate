package yayuki.heartrate.patient.data

import kotlinx.coroutines.flow.SharedFlow

interface LocationDataSource {
    val isConnect: Boolean
    val locationFlow: SharedFlow<Location>
    suspend fun connect()
}