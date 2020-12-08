package yayuki.heartrate.client.data

interface PatientState {
    val hrValue: Int
    val accuracy: Float
    val latitude: Double
    val longitude: Double
}