package org.example.config.oauth;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class NaverOAuth2DataResolver {

    @Value("${oauth2.naver.client-id}")
    private String clientId;

    @Value("${oauth2.naver.client-secret}")
    private String clientSecret;

    @Value("${oauth2.naver.client-name}")
    private String clientName;

    @Value("${oauth2.naver.authorization-grant-type}")
    private String authorizationType;

    @Value("${oauth2.naver.redirect-url}")
    private String redirectUrl;

    @Value("${oauth2.naver.authorization-uri}")
    private String authorizationUrl;

    @Value("${oauth2.naver.token-uri}")
    private String tokenUrl;

    @Value("${oauth2.naver.user-info-uri}")
    private String userInfoUrl;

    @Value("${oauth2.naver.user-name-attribute}")
    private String usernameAttribute;
}
