package yayuki.heartrate.carer.data

import yayuki.heartrate.client.data.PatientState

data class PatientStateEvent(
    val id: Int,
    override val hrValue: Int,
    override val accuracy: Float,
    override val latitude: Double,
    override val longitude: Double
) : PatientState