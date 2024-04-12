package org.example.support;

import org.example.exception.exceptions.BearerTokenNotFoundException;

public class JwtUtil {

    private static final String TOKEN_SEPARATOR = " ";
    private static final int BEARER_INDEX = 0;
    private static final int PAYLOAD_INDEX = 1;
    private static final String BEARER = "Bearer";

    public static void validateAuthorization(final String authorizationHeader) {
        if (!hasAuthorizationHeader(authorizationHeader) || !isBearerToken(authorizationHeader)) {
            throw new BearerTokenNotFoundException();
        }
    }

    private static boolean hasAuthorizationHeader(final String authorizationHeader) {
        return authorizationHeader != null;
    }

    private static boolean isBearerToken(final String authorizationHeader) {
        return authorizationHeader.split(TOKEN_SEPARATOR)[BEARER_INDEX].equals(BEARER);
    }

    public static String getJwtPayload(final String authorizationHeader) {
        return authorizationHeader.split(TOKEN_SEPARATOR)[PAYLOAD_INDEX];
    }
}
