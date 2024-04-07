package org.example.config.oauth;

import lombok.RequiredArgsConstructor;
import org.example.config.auth.CustomUserDetail;
import org.example.config.oauth.provider.NaverUserInfo;
import org.example.config.oauth.provider.OAuth2UserInfo;
import org.example.domain.entity.Member;
import org.example.domain.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PrincipalOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private final MemberRepository memberRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = initializeUser(userRequest);
        OAuth2UserInfo oAuth2UserInfo = createInfoByProvider(oAuth2User, userRequest);
        Member member = memberRepository.findByEmail(oAuth2UserInfo.getEmail())
                .orElse(createUser(oAuth2UserInfo));
        return new CustomUserDetail(member, oAuth2User.getAttributes());
    }

    private Member createUser(OAuth2UserInfo oAuth2UserInfo){
        Member member = Member.builder()
                .nickname(oAuth2UserInfo.getNickname())
                .email(oAuth2UserInfo.getEmail())
                .age(oAuth2UserInfo.getAge())
                .gender(oAuth2UserInfo.getGender())
                .ingredients("0")
                .build();
        memberRepository.save(member);
        return member;
    }

    private OAuth2User initializeUser(OAuth2UserRequest userRequest){
        return super.loadUser(userRequest);
    }

    private OAuth2UserInfo createInfoByProvider(OAuth2User oAuth2User, OAuth2UserRequest userRequest){
        if(userRequest.getClientRegistration().getRegistrationId().equals("google")){
            return new GoogleUserInfo(oAuth2User.getAttributes());
        }
        if(userRequest.getClientRegistration().getRegistrationId().equals("naver")){
            return new NaverUserInfo((Map) oAuth2User.getAttributes().get("response"));
        }
        return null;
    }
}
