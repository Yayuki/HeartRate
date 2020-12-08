package yayuki.heartrate.server.data;

import yayuki.heartrate.net.protobuf.CarerProto;
import yayuki.heartrate.net.protobuf.LoginProto;
import yayuki.heartrate.net.protobuf.PatientProto;
import yayuki.heartrate.net.protobuf.UserProto;

import java.util.stream.Collectors;

public class ResultUtil {
    public static LoginProto.Result errResult = LoginProto.Result
            .newBuilder()
            .setRetCode(1)
            .build();

    public static LoginProto.Result createPatientResult(Patient patient) {
        return createResultBuilder(patient)
                .setPatientExtra(createPatientExtra(patient))
                .build();
    }

    public static LoginProto.Result createCarerResult(Carer carer) {
        return createResultBuilder(carer)
                .setCarerExtra(createCarerExtra(carer))
                .build();
    }

    private static UserProto.User createUser(User user) {
        return createUser(user.getID(), user.getName());
    }

    private static UserProto.User createUser(int id, String name) {
        return UserProto.User.newBuilder()
                .setId(id)
                .setName(name)
                .build();
    }

    private static LoginProto.Result.Builder createResultBuilder(User user) {
        LoginProto.Result.Builder result = LoginProto.Result.newBuilder();
        result.setRetCode(-1)
                .setUser(createUser(user));
        return result;
    }

    private static PatientProto.PatientExtra createPatientExtra(Patient patient) {
        PatientProto.PatientExtra.Builder extra = PatientProto.PatientExtra.newBuilder();
        extra.setCarerName(patient.getCarerName())
                .setCarerPhone(patient.getCarerPhone());
        return extra.build();
    }

    private static CarerProto.CarerExtra createCarerExtra(Carer carer) {
        CarerProto.CarerExtra.Builder extra = CarerProto.CarerExtra.newBuilder();
        extra.addAllPatients(carer.getPatients().stream()
                .map(ResultUtil::createUser)
                .collect(Collectors.toList())
        );
        return extra.build();
    }
}
