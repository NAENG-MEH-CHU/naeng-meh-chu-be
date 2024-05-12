package org.example.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(staticName = "of")
@Getter
public class LoginResponse {

    private final String token;
    private final boolean isNew;
}
