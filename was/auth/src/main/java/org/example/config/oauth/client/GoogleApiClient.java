package org.example.config.oauth.client;

import lombok.NoArgsConstructor;
import org.example.config.oauth.params.OAuthLoginParams;
import org.example.config.oauth.params.OAuthProvider;
import org.example.config.oauth.provider.OAuth2UserInfo;
import org.example.config.oauth.provider.google.GoogleOAuth2DataResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class GoogleApiClient implements OAuthClient{

    @Autowired
    private GoogleOAuth2DataResolver resolver;

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.GOOGLE;
    }

    @Override
    public String requestAccessToken(OAuthLoginParams params) {
        return null;
    }

    @Override
    public OAuth2UserInfo requestOAuthInfo(String accessToken) {
        return null;
    }
}
