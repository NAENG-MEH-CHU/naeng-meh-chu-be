package org.example.config.oauth.provider.google;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.config.oauth.params.OAuthProvider;
import org.example.config.oauth.provider.OAuth2UserInfo;
import org.example.domain.enums.Gender;

import java.util.Date;

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
    public Date getAge() {
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
