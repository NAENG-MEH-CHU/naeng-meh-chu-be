package org.example.application;

import org.example.config.oauth.provider.naver.NaverOAuth2DataResolver;
import org.example.config.oauth.client.GoogleApiClient;
import org.example.config.oauth.client.NaverApiClient;
import org.example.config.oauth.params.OAuthLoginParams;
import org.example.config.oauth.provider.OAuth2UserInfo;
import org.example.config.oauth.provider.google.GoogleOAuth2DataResolver;
import org.example.domain.entity.Member;
import org.example.domain.repository.MemberRepository;
import org.example.infrastructure.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
public class OAuthLoginService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider tokenProvider;
    private final RequestOAuthInfoService requestOAuthInfoService;

    @Autowired
    private final NaverOAuth2DataResolver naverResolver;

    @Autowired
    private final GoogleOAuth2DataResolver googleResolver;

    @Autowired
    private NaverApiClient naverApiClient;

    @Autowired
    private GoogleApiClient googleApiClient;

    public OAuthLoginService(MemberRepository memberRepository,
                             JwtTokenProvider tokenProvider,
                             GoogleOAuth2DataResolver googleResolver,
                             NaverOAuth2DataResolver naverResolver,
                             GoogleApiClient googleApiClient,
                             NaverApiClient naverApiClient
                             ) {
        this.memberRepository = memberRepository;
        this.tokenProvider = tokenProvider;
        this.googleResolver = googleResolver;
        this.naverResolver = naverResolver;
        this.requestOAuthInfoService = new RequestOAuthInfoService(List.of(naverApiClient, googleApiClient));
    }

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

    public String getGoogleAuthorizeUrl() throws UnsupportedEncodingException {
        UriComponents uriComponents = UriComponentsBuilder
                .fromUriString(googleResolver.getAuthUrl())
                .queryParam("response_type", "code")
                .queryParam("client_id", googleResolver.getClientId())
                .queryParam("redirect_uri", URLEncoder.encode(googleResolver.getRedirectUrl(), StandardCharsets.UTF_8))
                .queryParam("scope", googleResolver.getScope())
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
                .id(null)
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
