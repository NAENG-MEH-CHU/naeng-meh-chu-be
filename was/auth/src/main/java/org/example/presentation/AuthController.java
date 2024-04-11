package org.example.presentation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.application.OAuthLoginService;
import org.example.config.oauth.params.NaverLoginParams;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OAuthLoginService oAuthLoginService;

    @GetMapping("/login/naver")
    public void naverLogin(HttpServletResponse response) throws UnsupportedEncodingException {
        String url = oAuthLoginService.getNaverAuthorizeUrl();
        try {
            response.sendRedirect(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @GetMapping("/naver/callback")
    public ResponseEntity<String> naverLoginCallback(@RequestParam String code, @RequestParam String state) {
        NaverLoginParams param = new NaverLoginParams(code, state);
        String authToken = oAuthLoginService.login(param);
        return new ResponseEntity<>(authToken, HttpStatus.CREATED);
    }
}
