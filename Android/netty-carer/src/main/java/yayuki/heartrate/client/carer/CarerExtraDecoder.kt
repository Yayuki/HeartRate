package yayuki.heartrate.client.carer

import yayuki.heartrate.carer.data.CarerExtra
import yayuki.heartrate.carer.data.Patient
import yayuki.heartrate.carer.data.PatientExtra
import yayuki.heartrate.client.data.Extra
import yayuki.heartrate.client.data.User
import yayuki.heartrate.client.handler.LoginHandler
import yayuki.heartrate.net.protobuf.LoginProto

class CarerExtraDecoder : LoginHandler.ExtraDecoder {
    override fun map(result: LoginProto.Result): Extra {
        val map = result.carerExtra.patientsList
            .map { Patient(User(it.id, it.name), PatientExtra()) }
            .associateByTo(HashMap(), { it.user.id }, { it })
        return CarerExtra(map)
    }
}