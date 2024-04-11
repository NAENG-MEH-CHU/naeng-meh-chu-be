package org.example.config.oauth.provider.google.token;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleToken (
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("expires_in")
        String expiresIn,
        @JsonProperty("scope")
        String scope,
        @JsonProperty("token_type")
        String tokenType,
        @JsonProperty("id_token")
        String idToken
){}
