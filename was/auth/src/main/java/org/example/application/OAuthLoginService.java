package org.example.application;

import org.example.config.oauth.params.OAuthProvider;
import org.example.config.oauth.provider.naver.NaverOAuth2DataResolver;
import org.example.config.oauth.client.GoogleApiClient;
import org.example.config.oauth.client.NaverApiClient;
import org.example.config.oauth.params.OAuthLoginParams;
import org.example.config.oauth.provider.OAuth2UserInfo;
import org.example.config.oauth.provider.google.GoogleOAuth2DataResolver;
import org.example.domain.entity.Member;
import org.example.domain.repository.MemberRepository;
import org.example.infrastructure.JwtTokenProvider;
import org.example.presentation.AuthControllerUtil;
import org.example.presentation.dto.InitializeMemberRequest;
import org.example.presentation.dto.LoginResponse;
import org.example.presentation.dto.TokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class OAuthLoginService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider tokenProvider;
    private final RequestOAuthInfoService requestOAuthInfoService;
    private final MemberReasonService memberReasonService;

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
                             NaverApiClient naverApiClient,
                             MemberReasonService memberReasonService
                             ) {
        this.memberRepository = memberRepository;
        this.tokenProvider = tokenProvider;
        this.googleResolver = googleResolver;
        this.naverResolver = naverResolver;
        this.requestOAuthInfoService = new RequestOAuthInfoService(List.of(naverApiClient, googleApiClient));
        this.memberReasonService = memberReasonService;
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

    @Transactional
    public String login(OAuthLoginParams params) {
        OAuth2UserInfo oAuthUserInfo = requestOAuthInfoService.request(params);
        Member memberId = findOrCreateUser(oAuthUserInfo);
        return tokenProvider.createAccessToken(memberId.getId().toString());
    }

    @Transactional
    public LoginResponse loginThroughApp(final String token, final String provider) {
        OAuthProvider oAuthProvider = findProvider(provider);
        OAuth2UserInfo oAuthUserInfo = requestOAuthInfoService.findThroughToken(oAuthProvider, parseToken(token));
        boolean isNew = isMemberEmpty(oAuthUserInfo.getEmail()); // 없으면 true임
        Member member = findOrCreateUser(oAuthUserInfo);
        isNew = isNew || !isMemberOnboarded(member);
        return LoginResponse.of(AuthControllerUtil.addPrefixToToken(tokenProvider.createAccessToken(member.getId().toString())), isNew);
    }

    @Transactional
    public TokenResponse initializeMember(final Member member, final InitializeMemberRequest request) {
        member.updateAge(request.paresAge());
        member.updateGender(request.parseGender());
        member.updateNickname(request.getNickname());
        memberRepository.save(member);
        memberReasonService.saveMemberReasons(member.getId(), request.parseReasons());
        return TokenResponse.of(AuthControllerUtil
                .addPrefixToToken(tokenProvider.createAccessToken(member.getId().toString())));
    }

    private boolean isMemberOnboarded(final Member member) {
        return member.isFinishedOnboarding() && memberReasonService.hasMemberReason(member.getId()); // 온보딩 다했으면 true임
    }

    private Member findOrCreateUser(OAuth2UserInfo oAuthUserInfo) {
        return memberRepository.findByEmail(oAuthUserInfo.getEmail())
                .orElseGet(() -> newUser(oAuthUserInfo));
    }

    private boolean isMemberEmpty(final String email) {
        return memberRepository.findByEmail(email).orElse(null) == null;
    }

    private Member newUser(OAuth2UserInfo oAuthUserInfo) {
        Member member = Member.builder()
                .id(null)
                .email(oAuthUserInfo.getEmail())
                .ingredients("0")
                .age(oAuthUserInfo.getAge())
                .gender(oAuthUserInfo.getGender())
                .nickname(oAuthUserInfo.getNickname())
                .build();
        return memberRepository.save(member);
    }

    private OAuthProvider findProvider(final String provider) {
        if(provider.equals(OAuthProvider.NAVER.getValue())) {
            return OAuthProvider.NAVER;
        }

        return OAuthProvider.GOOGLE;
    }

    private String parseToken(final String token) {
        if(token.startsWith("Bearer ")) {
            return token;
        }

        return "Bearer " + token;
    }
}
