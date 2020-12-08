package yayuki.heartrate.patient.data

import yayuki.heartrate.client.data.Extra

data class PatientExtra(
    val carerName: String,
    val carerPhone: String
) : Extra
