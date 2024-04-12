package org.example.presentation;

import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.example.config.oauth.params.OAuthLoginParams;
import org.example.config.oauth.params.google.GoogleLoginParams;
import org.example.config.oauth.params.naver.NaverLoginParams;

@NoArgsConstructor
public class AuthControllerUtil {

    private static final String PREFIX = "Bearer ";

    public static OAuthLoginParams createOAuthLoginParams(final String code, String state) {
        if(state == null) return new GoogleLoginParams(code);
        return new NaverLoginParams(code, state);
    }

    public static void sendToRedirect(final String url, final HttpServletResponse response) {
        try {
            response.sendRedirect(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String addPrefixToToken(final String authToken) {
        return PREFIX + authToken;
    }
}
