package org.example.application;

import lombok.AllArgsConstructor;
import org.example.config.oauth.NaverOAuth2DataResolver;
import org.example.domain.entity.Member;
import org.example.domain.repository.MemberRepository;
import org.example.exception.exceptions.MemberNotFoundException;
import org.example.infrastructure.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@AllArgsConstructor
public class JwtAuthService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    private NaverOAuth2DataResolver naverResolver;

    @Transactional(readOnly = true)
    public Member findMemberByJwtPayload(final String jwtPayload) {
        String jwtPayloadOfEmail = jwtTokenProvider.getPayload(jwtPayload);
        return memberRepository.findByEmail(jwtPayloadOfEmail)
                .orElseThrow(MemberNotFoundException::new);
    }

    public String getNaverAuthorizeUrl() throws URISyntaxException, MalformedURLException, UnsupportedEncodingException {
        UriComponents uriComponents = UriComponentsBuilder
                .fromUriString("https://nid.naver.com/oauth2.0/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", naverResolver.getClientId())
                .queryParam("redirect_uri", URLEncoder.encode(naverResolver.getRedirectUrl(), StandardCharsets.UTF_8))
                .queryParam("state", URLEncoder.encode("1234", StandardCharsets.UTF_8))
                .build();
        return uriComponents.toString();
    }
}
