package yayuki.heartrate.server.database;

import yayuki.heartrate.server.data.Carer;
import yayuki.heartrate.server.data.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public enum MySQL {
    INSTANCE;
    private static final String DB_PATIENT_LOGIN = "{CALL heartrate.patient_login (?, ?, ?, ?, ?, ?, ?, ?)}";
    private static final String DB_CARER_LOGIN = "{CALL heartrate.carer_login (?, ?, ?, ?, ?)}";
    private static final String P_ACCOUNT = "account";
    private static final String P_PASSWORD = "password";
    private static final String P_IS_OK = "is_ok";
    private static final String P_USER_ID = "user_id";
    private static final String P_USER_NAME = "user_name";
    private static final String P_CARER_ID = "carer_id";
    private static final String P_CARER_NAME = "carer_name";
    private static final String P_CARER_PHONE = "carer_phone";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setUser(String account, String password, CallableStatement s) {
        try {
            s.setString(P_ACCOUNT, account);
            s.setString(P_PASSWORD, password);
            s.registerOutParameter(P_IS_OK, Types.BOOLEAN);
            s.registerOutParameter(P_USER_ID, Types.INTEGER);
            s.registerOutParameter(P_USER_NAME, Types.VARCHAR);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<Carer> carerLogin(String account, String password) {
        Connection connection = null;
        CallableStatement stmt = null;
        ResultSet resultSet = null;
        try {
            connection = DBCPUtils.INSTANCE.getConnection();
            stmt = connection.prepareCall(DB_CARER_LOGIN);
            setUser(account, password, stmt);
            resultSet = stmt.executeQuery();
            if (!stmt.getBoolean("is_ok")) return Optional.empty();

            Carer carer = new Carer();
            carer.setID(stmt.getInt(P_USER_ID));
            carer.setName(stmt.getString(P_USER_NAME));
            ArrayList<Patient> patients = new ArrayList<>();
            while (resultSet.next()) {
                Patient p = new Patient();
                p.setID(resultSet.getInt(1));
                p.setName(resultSet.getString(2));
                patients.add(p);
            }
            carer.setPatients(patients);
            stmt.close();
            resultSet.close();
            connection.close();
            return Optional.of(carer);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (stmt != null) stmt.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.err.println(e.toString());
            }
        }
        return Optional.empty();

    }

    public Optional<Patient> patientLogin(String account, String password) {
        Connection connection = null;
        CallableStatement stmt = null;
        try {
            connection = DBCPUtils.INSTANCE.getConnection();
            stmt = connection.prepareCall(DB_PATIENT_LOGIN);
            setUser(account, password, stmt);
            stmt.registerOutParameter(P_CARER_ID, Types.INTEGER);
            stmt.registerOutParameter(P_CARER_NAME, Types.VARCHAR);
            stmt.registerOutParameter(P_CARER_PHONE, Types.VARCHAR);
            stmt.execute();
            if (!stmt.getBoolean("is_ok")) return Optional.empty();

            Patient patient = new Patient();
            patient.setID(stmt.getInt(P_USER_ID));
            patient.setName(stmt.getString(P_USER_NAME));
            patient.setCarerID(stmt.getInt(P_CARER_ID));
            patient.setCarerName(stmt.getString(P_CARER_NAME));
            patient.setCarerPhone(stmt.getString(P_CARER_PHONE));
            return Optional.of(patient);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.err.println(e.toString());
            }
        }
        return Optional.empty();
    }
}
