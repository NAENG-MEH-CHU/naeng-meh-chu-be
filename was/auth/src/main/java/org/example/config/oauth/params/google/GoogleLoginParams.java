package org.example.config.oauth.params.google;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.config.oauth.params.OAuthLoginParams;
import org.example.config.oauth.params.OAuthProvider;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GoogleLoginParams implements OAuthLoginParams {

    private String code;

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.GOOGLE;
    }

    @Override
    public MultiValueMap<String, String> makeBody() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        return body;
    }
}
