package org.example.config.oauth.provider.naver;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.config.oauth.params.OAuthProvider;
import org.example.config.oauth.provider.OAuth2UserInfo;
import org.example.domain.enums.Gender;

import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo {

    @JsonProperty(value = "response")
    private Response response;

    @Override
    public String getEmail() {
        return response.email();
    }

    @Override
    public String getNickname() {
        return response.nickname();
    }

    @Override
    public int getAge() {
        return 0;
    }

    @Override
    public Gender getGender() {
        String genderIn = response.gender();
        if(genderIn.equals("F")) return Gender.FEMALE;
        if(genderIn.equals("M")) return Gender.MALE;
        return null;
    }

    @Override
    public OAuthProvider getOAuthProvider() {
        return OAuthProvider.NAVER;
    }
}
