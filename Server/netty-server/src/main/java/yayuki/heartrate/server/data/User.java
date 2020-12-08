package yayuki.heartrate.server.data;

import io.netty.util.AttributeKey;

public interface User {
    AttributeKey<UserStatus> ATTR_STATUS = AttributeKey.valueOf("userStatus");
    AttributeKey<Integer> ATTR_ID = AttributeKey.valueOf("userID");

    void setID(int id);

    int getID();

    void setName(String name);

    String getName();
}
