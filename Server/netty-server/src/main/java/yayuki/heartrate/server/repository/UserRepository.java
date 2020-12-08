package yayuki.heartrate.server.repository;

import yayuki.heartrate.server.data.Carer;
import yayuki.heartrate.server.data.Patient;
import yayuki.heartrate.server.database.MySQL;

import java.util.Optional;

public enum UserRepository {
    INSTANCE;

    public Optional<Carer> loginCarer(String account, String password) {
        return MySQL.INSTANCE.carerLogin(account, password);
    }

    public Optional<Patient> loginPatient(String account, String password) {
        return MySQL.INSTANCE.patientLogin(account, password);
    }
}