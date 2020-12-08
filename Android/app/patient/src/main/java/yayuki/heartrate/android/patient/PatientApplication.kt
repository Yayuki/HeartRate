package yayuki.heartrate.android.patient

import yayuki.heartrate.android.patient.data.BleHRDevice
import yayuki.heartrate.android.patient.data.GoogleGps
import yayuki.heartrate.android.user.BaseApplication
import yayuki.heartrate.client.patient.NettyPatientClient
import yayuki.heartrate.patient.data.HRDataSource
import yayuki.heartrate.patient.data.LocationDataSource
import yayuki.heartrate.patient.repo.PatientRepository

class PatientApplication : BaseApplication() {
    override val client = NettyPatientClient()
    private val hrData: HRDataSource by lazy {
        BleHRDevice(applicationContext)
    }
    private val locationData: LocationDataSource by lazy {
        GoogleGps(applicationContext)
    }
    override val userRepository by lazy {
        PatientRepository(client, hrData, locationData)
    }
}