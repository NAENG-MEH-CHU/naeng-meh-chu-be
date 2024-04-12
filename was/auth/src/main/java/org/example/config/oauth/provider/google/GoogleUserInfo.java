package org.example.config.oauth.provider.google;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.config.oauth.params.OAuthProvider;
import org.example.config.oauth.provider.OAuth2UserInfo;
import org.example.domain.enums.Gender;

public class GoogleUserInfo implements OAuth2UserInfo {

    @JsonProperty("id")
    private String email;

    @JsonProperty("name")
    private String nickname;

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public int getAge() {
        return 0;
    }

    @Override
    public Gender getGender() {
        return null;
    }

    @Override
    public OAuthProvider getOAuthProvider() {
        return OAuthProvider.GOOGLE;
    }
}
