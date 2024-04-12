package org.example.config.oauth.provider.google;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class GoogleOAuth2DataResolver {

    @Value("${oauth2.google.client-id}")
    private String clientId;

    @Value("${oauth2.google.client-secret}")
    private String clientSecret;

    @Value("${oauth2.google.authorization-uri}")
    private String authUrl;

    @Value("${oauth2.google.redirect-uri}")
    private String redirectUrl;

    @Value("${oauth2.google.token-uri}")
    private String tokenUrl;

    @Value("${oauth2.google.resource-uri}")
    private String userInfoUrl;

    @Value("${oauth2.google.scope}")
    private String scope;
}
