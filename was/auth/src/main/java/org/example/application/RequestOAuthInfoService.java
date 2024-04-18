package org.example.application;

import org.example.config.oauth.client.OAuthClient;
import org.example.config.oauth.params.OAuthLoginParams;
import org.example.config.oauth.params.OAuthProvider;
import org.example.config.oauth.provider.OAuth2UserInfo;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RequestOAuthInfoService {

    private final Map<OAuthProvider, OAuthClient> clients;

    public RequestOAuthInfoService(List<OAuthClient> clients) {
        this.clients = clients.stream().collect(
                Collectors.toUnmodifiableMap(OAuthClient::oAuthProvider, Function.identity())
        );
    }

    public OAuth2UserInfo request(OAuthLoginParams params) {
        OAuthClient client = clients.get(params.oAuthProvider());
        String accessToken = client.requestAccessToken(params);
        return client.requestOAuthInfo(accessToken);
    }

    public OAuth2UserInfo findThroughToken(OAuthProvider provider, String token) {
        OAuthClient client = clients.get(provider);
        return client.requestOAuthInfo(token);
    }
}
