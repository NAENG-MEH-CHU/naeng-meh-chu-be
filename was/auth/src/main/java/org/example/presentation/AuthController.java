package org.example.presentation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.application.JwtAuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtAuthService jwtAuthService;

    @GetMapping("/login/naver")
    public void loginNaver(HttpServletRequest request, HttpServletResponse response) throws MalformedURLException, UnsupportedEncodingException, URISyntaxException {
        String url = jwtAuthService.getNaverAuthorizeUrl();
        try {
            response.sendRedirect(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
