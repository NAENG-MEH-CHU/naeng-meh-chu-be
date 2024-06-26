package org.example.config.oauth.provider.naver.token;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NaverToken(
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("refresh_token")
        String refreshToken,
        @JsonProperty("token_type")
        String tokenType,
        @JsonProperty("expires_in")
        String expiresIn
) {
}


