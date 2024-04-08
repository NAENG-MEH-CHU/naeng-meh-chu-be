package org.example.application;

import lombok.RequiredArgsConstructor;
import org.example.config.oauth.NaverOAuth2DataResolver;
import org.example.config.oauth.params.OAuthLoginParams;
import org.example.config.oauth.provider.OAuth2UserInfo;
import org.example.domain.entity.Member;
import org.example.domain.repository.MemberRepository;
import org.example.infrastructure.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider tokenProvider;
    private final RequestOAuthInfoService requestOAuthInfoService;

    @Autowired
    private NaverOAuth2DataResolver naverResolver;

    public String getNaverAuthorizeUrl() throws UnsupportedEncodingException {
        UriComponents uriComponents = UriComponentsBuilder
                .fromUriString(naverResolver.getAuthorizationUrl())
                .queryParam("response_type", "code")
                .queryParam("client_id", naverResolver.getClientId())
                .queryParam("redirect_uri", URLEncoder.encode(naverResolver.getRedirectUrl(), StandardCharsets.UTF_8))
                .queryParam("state", URLEncoder.encode("1234", StandardCharsets.UTF_8))
                .build();
        return uriComponents.toString();
    }

    public String login(OAuthLoginParams params) {
        OAuth2UserInfo oAuthUserInfo = requestOAuthInfoService.request(params);
        UUID memberId = findOrCreateUser(oAuthUserInfo);
        return tokenProvider.createAccessToken(memberId.toString());
    }

    private UUID findOrCreateUser(OAuth2UserInfo oAuthUserInfo) {
        return memberRepository.findByEmail(oAuthUserInfo.getEmail())
                .map(Member::getId)
                .orElseGet(() -> newUser(oAuthUserInfo));
    }

    private UUID newUser(OAuth2UserInfo oAuthUserInfo) {
        Member member = Member.builder()
                .email(oAuthUserInfo.getEmail())
                .ingredients("0")
                .age(oAuthUserInfo.getAge())
                .gender(oAuthUserInfo.getGender())
                .nickname(oAuthUserInfo.getNickname())
                .build();

        memberRepository.save(member);
        return member.getId();
    }
}
