package yayuki.heartrate.client

interface PatientClient : UserClient {
    fun update(id: Int, hrValue: Int, accuracy: Float, latitude: Double, longitude: Double)
}