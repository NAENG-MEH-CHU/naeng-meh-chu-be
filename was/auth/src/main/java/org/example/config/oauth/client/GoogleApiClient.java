package org.example.config.oauth.client;

import lombok.NoArgsConstructor;
import org.example.config.oauth.params.OAuthLoginParams;
import org.example.config.oauth.params.OAuthProvider;
import org.example.config.oauth.provider.OAuth2UserInfo;
import org.example.config.oauth.provider.google.GoogleOAuth2DataResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Component
@NoArgsConstructor
public class GoogleApiClient implements OAuthClient{

    @Autowired
    private GoogleOAuth2DataResolver resolver;
    private static final RestTemplate restTemplate = new RestTemplate();

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.GOOGLE;
    }

    @Override
    public String requestAccessToken(OAuthLoginParams params) {
        HttpEntity<MultiValueMap<String, String>> request = generateHttpRequest(params);
        System.out.println(restTemplate.postForObject(resolver.getTokenUrl(), request, String.class));
        return null;
    }

    @Override
    public OAuth2UserInfo requestOAuthInfo(String accessToken) {
        return null;
    }

    private HttpEntity<MultiValueMap<String, String>> generateHttpRequest(OAuthLoginParams params) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = params.makeBody();
        body.add("grant_type", "authorization_code");
        body.add("client_id", resolver.getClientId());
        body.add("client_secret", resolver.getClientSecret());
        body.add("redirect_uri", resolver.getRedirectUrl());
        return new HttpEntity<>(body, httpHeaders);
    }
}
