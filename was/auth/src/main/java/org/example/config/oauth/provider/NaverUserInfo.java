package org.example.config.oauth.provider;

import org.example.domain.enums.Gender;

import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo{
    private Map<String, Object> attributes; //oAuth2User.getAttributes()를 받는다.

    public NaverUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getEmail() {
        return (String)attributes.get("email");
    }

    @Override
    public String getNickname() {
        return (String)attributes.get("nickname");
    }

    @Override
    public int getAge() {
        return Integer.parseInt((String)attributes.get("age"));
    }

    @Override
    public Gender getGender() {
        String genderIn = (String)attributes.get("gender");
        if(genderIn.equals("F")) return Gender.FEMALE;
        if(genderIn.equals("M")) return Gender.MALE;
        return null;
    }
}
