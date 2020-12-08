package yayuki.heartrate.carer.data

import yayuki.heartrate.client.data.Extra
import yayuki.heartrate.client.data.User

data class CarerExtra(
    private val map: Map<Int, Patient>
) : Extra {
    internal val notFound = Patient(User(-1, "NotFount"), PatientExtra())
    val patientList get() = map.values.toList()

    fun getPatient(id: Int): Patient {
        return map[id] ?: notFound
    }

    fun getPatientExtra(id: Int): PatientExtra {
        return map[id]?.extra ?: notFound.extra
    }
}
