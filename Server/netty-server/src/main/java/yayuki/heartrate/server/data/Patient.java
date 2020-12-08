package yayuki.heartrate.server.data;

import io.netty.util.AttributeKey;

public class Patient implements User {
    private int ID;
    private String name;
    private int carerID;
    private String carerName;
    private String carerPhone;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCarerID() {
        return carerID;
    }

    public void setCarerID(int carerID) {
        this.carerID = carerID;
    }

    public String getCarerName() {
        return carerName;
    }

    public void setCarerName(String carerName) {
        this.carerName = carerName;
    }

    public String getCarerPhone() {
        return carerPhone;
    }

    public void setCarerPhone(String carerPhone) {
        this.carerPhone = carerPhone;
    }
}
