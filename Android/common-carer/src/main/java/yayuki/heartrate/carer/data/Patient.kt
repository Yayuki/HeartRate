package yayuki.heartrate.carer.data

import yayuki.heartrate.client.data.User

data class Patient(
    val user: User,
    val extra: PatientExtra
)