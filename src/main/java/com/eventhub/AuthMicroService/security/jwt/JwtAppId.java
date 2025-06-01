package com.eventhub.AuthMicroService.security.jwt;

import java.util.UUID;

public class JwtAppId {
    private static UUID id;

    public static UUID setSessionId() {
        id = UUID.randomUUID();
        return id;
    }

    public static String getSessionId() {
        return id.toString();
    }
}
