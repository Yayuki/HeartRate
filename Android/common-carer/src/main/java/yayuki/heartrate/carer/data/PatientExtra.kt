package yayuki.heartrate.carer.data

import yayuki.heartrate.client.data.Extra
import yayuki.heartrate.client.data.PatientState

data class PatientExtra(
    internal var _hrValue: Int = 0,
    internal var _accuracy: Float = .0f,
    internal var _latitude: Double = .0,
    internal var _longitude: Double = .0
) : Extra, PatientState {
    override val hrValue: Int
        get() = _hrValue
    override val accuracy: Float
        get() = _accuracy
    override val latitude: Double
        get() = _latitude
    override val longitude: Double
        get() = _longitude
}
