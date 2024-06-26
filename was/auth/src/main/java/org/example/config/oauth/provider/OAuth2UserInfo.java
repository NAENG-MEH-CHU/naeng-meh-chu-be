package org.example.config.oauth.provider;

import org.example.config.oauth.params.OAuthProvider;
import org.example.domain.enums.Age;
import org.example.domain.enums.Gender;

import java.time.LocalDate;
import java.util.Date;

public interface OAuth2UserInfo {
    String getEmail();
    String getNickname();
    Age getAge();
    Gender getGender();
    OAuthProvider getOAuthProvider();
}
