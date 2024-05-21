package org.example.config.oauth.provider.naver;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Response(
        @JsonProperty("id")
        String id,
        @JsonProperty("email")
        String email,
        @JsonProperty("nickname")
        String nickname,
        @JsonProperty("age")
        String age,
        @JsonProperty("gender")
        String gender
){}
