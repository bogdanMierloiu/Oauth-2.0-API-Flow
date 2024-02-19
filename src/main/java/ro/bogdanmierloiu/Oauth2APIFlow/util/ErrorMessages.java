package ro.bogdanmierloiu.Oauth2APIFlow.util;

import java.util.UUID;

public class ErrorMessages {

    private ErrorMessages() {
    }
    public static String objectWithUuidNotFound(String objectType, UUID objectUuid) {
        return String.format("%s with uuid: %s not found!", objectType, objectUuid.toString());
    }

}
