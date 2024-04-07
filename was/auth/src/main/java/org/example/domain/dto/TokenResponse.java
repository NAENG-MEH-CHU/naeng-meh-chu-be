package main.java.org.example.domain.dto;

public class TokenResponse {

    private String accessToken;

    private TokenResponse() {
    }

    private TokenResponse(final String accessToken) {
        this.accessToken = accessToken;
    }

    public static TokenResponse from(final String accessToken) {
        return new TokenResponse(accessToken);
    }

    public String getAccessToken() {
        return accessToken;
    }
}
