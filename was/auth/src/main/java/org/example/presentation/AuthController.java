package org.example.presentation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.example.application.JwtAuthService;
import org.example.application.OAuthLoginService;
import org.example.config.oauth.params.OAuthLoginParams;
import org.example.domain.entity.Member;
import org.example.presentation.dto.OAuthLoginRequest;
import org.example.presentation.dto.TokenResponse;
import org.example.support.JwtLogin;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String GOOGLE = "google";
    private final OAuthLoginService oAuthLoginService;
    private final JwtAuthService jwtAuthService;

    @GetMapping("/login/{provider}")
    public void loginThroughOAuth2(HttpServletResponse response, @PathVariable("provider") String provider) throws UnsupportedEncodingException {
        AuthControllerUtil.sendToRedirect(getUrlByProvider(provider), response);
    }

    @GetMapping("/{provider}/callback")
    public ResponseEntity<TokenResponse> loginCallback(@RequestParam String code,
                                                       @RequestParam(required = false) String state,
                                                       @PathVariable("provider") String provider) {
        OAuthLoginParams param = AuthControllerUtil.createOAuthLoginParams(code, state);
        String token = AuthControllerUtil.addPrefixToToken(oAuthLoginService.login(param));
        return new ResponseEntity<>(TokenResponse.of(token), HttpStatus.CREATED);
    }

    @PostMapping("/login/{provider}")
    public ResponseEntity<TokenResponse> loginThroughApp(@RequestBody @Valid final OAuthLoginRequest request,
                                                         @PathVariable("provider") String provider) {
        String token = AuthControllerUtil.addPrefixToToken(oAuthLoginService.loginThroughApp(request, provider));
        return new ResponseEntity<>(TokenResponse.of(token), HttpStatus.CREATED);
    }

    @GetMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String reissuedToken = AuthControllerUtil.addPrefixToToken(jwtAuthService.reissue(token));
        return new ResponseEntity<>(TokenResponse.of(reissuedToken), HttpStatus.CREATED);
    }

    private String getUrlByProvider(final String provider) throws UnsupportedEncodingException {
        if(provider.equals(GOOGLE)) {
            return oAuthLoginService.getGoogleAuthorizeUrl();
        }
        return oAuthLoginService.getNaverAuthorizeUrl();
    }
}
