package org.example.config.oauth.client;

import lombok.NoArgsConstructor;
import org.example.config.oauth.params.OAuthLoginParams;
import org.example.config.oauth.params.OAuthProvider;
import org.example.config.oauth.provider.OAuth2UserInfo;
import org.example.config.oauth.provider.google.GoogleOAuth2DataResolver;
import org.example.config.oauth.provider.google.GoogleUserInfo;
import org.example.config.oauth.provider.google.token.GoogleToken;
import org.example.config.oauth.provider.naver.NaverUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

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
        GoogleToken googleToken = restTemplate.postForObject(resolver.getTokenUrl(), request, GoogleToken.class);
        Objects.requireNonNull(googleToken);
        return googleToken.accessToken();
    }

    @Override
    public OAuth2UserInfo requestOAuthInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken);
        System.out.println(("Authorization: " + accessToken));

        HttpEntity request = new HttpEntity(headers);
        System.out.println(request.toString());
        ResponseEntity<GoogleUserInfo> response = restTemplate.exchange(
                resolver.getUserInfoUrl(),
                HttpMethod.GET,
                request,
                GoogleUserInfo.class
        );
        System.out.println(response.toString());
        return response.getBody();
    }

    private String createUri(final String accessToken, final String url) {
        UriComponents uriComponents = UriComponentsBuilder
                .fromUriString(url)
                .queryParam("access_token", accessToken)
                .build();
        System.out.println(uriComponents.toString());
        return uriComponents.toString();
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
