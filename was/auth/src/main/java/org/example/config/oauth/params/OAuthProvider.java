package org.example.config.oauth.params;

import lombok.Getter;

@Getter
public enum OAuthProvider {

    NAVER("naver"),
    GOOGLE("google");

    private final String value;

    OAuthProvider(String value) {
        this.value = value;
    }
}
