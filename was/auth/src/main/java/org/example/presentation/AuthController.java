package org.example.presentation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.application.JwtAuthService;
import org.example.application.OAuthLoginService;
import org.example.config.oauth.params.OAuthLoginParams;
import org.example.domain.entity.Member;
import org.example.support.JwtLogin;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OAuthLoginService oAuthLoginService;
    private final JwtAuthService jwtAuthService;

    @GetMapping("/login/naver")
    public void naverLogin(HttpServletResponse response) throws UnsupportedEncodingException {
        String url = oAuthLoginService.getNaverAuthorizeUrl();
        AuthControllerUtil.sendToRedirect(url, response);
    }

    @GetMapping("/login/google")
    public void googleLogin(HttpServletResponse response) throws UnsupportedEncodingException {
        String url = oAuthLoginService.getGoogleAuthorizeUrl();
        AuthControllerUtil.sendToRedirect(url, response);
    }

    @GetMapping("/{provider}/callback")
    public ResponseEntity<String> loginCallback(@RequestParam String code,
                                                @RequestParam(required = false) String state,
                                                @PathVariable("provider") String provider) {
        OAuthLoginParams param = AuthControllerUtil.createOAuthLoginParams(code, state);
        String authToken = oAuthLoginService.login(param);
        return new ResponseEntity<>(authToken, HttpStatus.CREATED);
    }

    @GetMapping("/find")
    public ResponseEntity<Member> findMemberByToken(@JwtLogin Member member) {
        return new ResponseEntity<>(member, HttpStatus.OK);
    }
}
