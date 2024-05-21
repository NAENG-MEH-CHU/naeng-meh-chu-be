package org.example.config.oauth.client;

import lombok.NoArgsConstructor;
import org.example.config.oauth.params.OAuthLoginParams;
import org.example.config.oauth.params.OAuthProvider;
import org.example.config.oauth.provider.OAuth2UserInfo;
import org.example.config.oauth.provider.google.GoogleUserInfo;
import org.example.config.oauth.provider.naver.NaverUserInfo;
import org.example.config.oauth.provider.naver.token.NaverToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Component
@NoArgsConstructor
public class NaverApiClient implements OAuthClient {

    @Value("${oauth2.naver.token-uri}")
    private String authUrl;
    @Value("${oauth2.naver.user-info-uri}")
    private String apiUrl;
    @Value("${oauth2.naver.client-id}")
    private String clientId;
    @Value("${oauth2.naver.client-secret}")
    private String clientSecret;
    private static final RestTemplate restTemplate = new RestTemplate();

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.NAVER;
    }

    @Override
    public String requestAccessToken(OAuthLoginParams params) {
        HttpEntity<MultiValueMap<String, String>> request = generateHttpRequest(params);
        NaverToken naverToken = restTemplate.postForObject(authUrl, request, NaverToken.class);
        Objects.requireNonNull(naverToken);
        return naverToken.accessToken();
    }

    @Override
    public OAuth2UserInfo requestOAuthInfo(String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",accessToken);
        System.out.println(("Authorization: "  + accessToken));

        HttpEntity request = new HttpEntity(headers);
        System.out.println(request.toString());
        ResponseEntity<NaverUserInfo> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                request,
                NaverUserInfo.class
        );
        System.out.println(response.toString());
        return response.getBody();
    }

    private String addBearerPrefix(String accessToken) {
        if(accessToken.startsWith("Bearer ")) return accessToken;
        return "Bearer " + accessToken;
    }

    private HttpEntity<MultiValueMap<String, String>> generateHttpRequest(OAuthLoginParams params) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = params.makeBody();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        return new HttpEntity<>(body, httpHeaders);
    }
}
