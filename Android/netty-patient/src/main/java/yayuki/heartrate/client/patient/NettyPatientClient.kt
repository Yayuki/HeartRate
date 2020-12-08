package yayuki.heartrate.client.patient

import yayuki.heartrate.client.NettyUserClient
import yayuki.heartrate.client.PatientClient
import yayuki.heartrate.net.protobuf.LoginProto
import yayuki.heartrate.net.protobuf.PatientProto

class NettyPatientClient : NettyUserClient(
    LoginProto.Request.Status.PATIENT,
    PatientExtraDecoder()
), PatientClient {
    override fun update(
        id: Int, hrValue: Int,
        accuracy: Float, latitude: Double, longitude: Double
    ) {
        send(createPatientState(id, hrValue, accuracy, latitude, longitude))
    }

    private fun createPatientState(
        id: Int, hrValue: Int,
        accuracy: Float, latitude: Double, longitude: Double
    ): PatientProto.State {
        return PatientProto.State.newBuilder()
            .setId(id)
            .setHeartRate(hrValue)
            .setAccuracy(accuracy)
            .setLatitude(latitude)
            .setLongitude(longitude)
            .build()
    }
}