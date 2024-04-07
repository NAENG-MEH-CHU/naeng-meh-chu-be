package org.example.config.oauth.provider;

import org.example.domain.enums.Gender;

public interface OAuth2UserInfo {
    String getEmail();
    String getNickname();
    int getAge();
    Gender getGender();
}
