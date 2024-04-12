package org.example.presentation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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

    private static final String GOOGLE = "google";
    private final OAuthLoginService oAuthLoginService;

    @GetMapping("/login/{provider}")
    public void loginThroughOAuth2(HttpServletResponse response, @PathVariable("provider") String provider) throws UnsupportedEncodingException {
        AuthControllerUtil.sendToRedirect(getUrlByProvider(provider), response);
    }

    @GetMapping("/{provider}/callback")
    public ResponseEntity<String> loginCallback(@RequestParam String code,
                                                @RequestParam(required = false) String state,
                                                @PathVariable("provider") String provider) {
        OAuthLoginParams param = AuthControllerUtil.createOAuthLoginParams(code, state);
        String authToken = oAuthLoginService.login(param);
        return new ResponseEntity<>(AuthControllerUtil.addPrefixToToken(authToken), HttpStatus.CREATED);
    }

    @GetMapping("/find")
    public ResponseEntity<Member> findMemberByToken(@JwtLogin final Member member) {
        return new ResponseEntity<>(member, HttpStatus.OK);
    }

    private String getUrlByProvider(final String provider) throws UnsupportedEncodingException {
        if(provider.equals(GOOGLE)) {
            return oAuthLoginService.getGoogleAuthorizeUrl();
        }
        return oAuthLoginService.getNaverAuthorizeUrl();
    }
}
