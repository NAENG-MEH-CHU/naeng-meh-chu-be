package org.example.presentation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.application.JwtAuthService;
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

    @PostMapping("/login/naver")
    public void naverLogin(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        String url = oAuthLoginService.getNaverAuthorizeUrl();
        try {
            response.sendRedirect(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @GetMapping("/login/oauth2/code/naver")
    public ResponseEntity<String> naverLogin(@RequestBody NaverLoginParams param) {
        String authToken = oAuthLoginService.login(param);
        return new ResponseEntity<>(authToken, HttpStatus.OK);
    }
}
