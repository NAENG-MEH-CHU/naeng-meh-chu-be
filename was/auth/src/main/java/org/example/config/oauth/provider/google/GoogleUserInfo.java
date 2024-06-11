package org.example.config.oauth.provider.google;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.config.oauth.params.OAuthProvider;
import org.example.config.oauth.provider.OAuth2UserInfo;
import org.example.domain.enums.Age;
import org.example.domain.enums.Gender;

public class GoogleUserInfo implements OAuth2UserInfo {

    @JsonProperty("id")
    private String email;

    @JsonProperty("name")
    private String nickname;

    @Override
    public String getEmail() {
        if(email == null || email.isEmpty()) return nickname;

        return email;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public Age getAge() {
        return null;
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
