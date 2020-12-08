package yayuki.heartrate.client.patient

import yayuki.heartrate.client.data.Extra
import yayuki.heartrate.client.handler.LoginHandler
import yayuki.heartrate.net.protobuf.LoginProto
import yayuki.heartrate.patient.data.PatientExtra

class PatientExtraDecoder : LoginHandler.ExtraDecoder {
    override fun map(result: LoginProto.Result): Extra {
        val protoExtra = result.patientExtra
        return PatientExtra(protoExtra.carerName, protoExtra.carerPhone)
    }
}