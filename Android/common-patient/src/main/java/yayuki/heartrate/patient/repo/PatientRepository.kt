package yayuki.heartrate.patient.repo

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import yayuki.heartrate.client.PatientClient
import yayuki.heartrate.client.repo.BaseUserRepository
import yayuki.heartrate.patient.data.HRDataSource
import yayuki.heartrate.patient.data.Location
import yayuki.heartrate.patient.data.LocationDataSource
import yayuki.heartrate.patient.data.PatientExtra

class PatientRepository(
    private val client: PatientClient,
    private val hrData: HRDataSource,
    private val locationData: LocationDataSource
) : BaseUserRepository<PatientExtra>(
    PatientExtra::class.java,
    client
) {
    override var _extra: PatientExtra = PatientExtra("", "")

    val hrValueFlow get() = hrData.hrValueFlow

    suspend fun connect() {
        if (!hrData.isConnect) hrData.connect()
        if (!locationData.isConnect) locationData.connect()
        GlobalScope.launch {
            hrData.hrValueFlow
                .combine(
                    locationData.locationFlow
                ) { hrValue: Int, location: Location ->
                    Pair(hrValue, location)
                }.conflate().onEach {
                    delay(5000)
                }.collect {
                    println("${it.first} ${it.second}")
                    client.update(
                        user.id,
                        it.first,
                        it.second.accuracy,
                        it.second.latitude,
                        it.second.longitude
                    )
                }
        }
    }
}