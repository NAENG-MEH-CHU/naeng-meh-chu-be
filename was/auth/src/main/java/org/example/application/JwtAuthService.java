package org.example.application;

import lombok.AllArgsConstructor;
import org.example.domain.entity.Member;
import org.example.domain.repository.MemberRepository;
import org.example.exception.exceptions.MemberNotFoundException;
import org.example.exception.exceptions.NeedToLoginException;
import org.example.infrastructure.JwtTokenProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class JwtAuthService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(readOnly = true)
    public Member findMemberByJwtPayload(final String token) {
        jwtTokenProvider.validateToken(token);
        String id = jwtTokenProvider.getPayload(token);
        return findMemberById(UUID.fromString(id));
    }

    @Transactional(readOnly = true)
    public String reissue(final String token) {
        if(token == null) throw new NeedToLoginException();
        String id = jwtTokenProvider.getPayload(token);
        UUID memberId = findMemberById(UUID.fromString(id)).getId();
        return jwtTokenProvider.createAccessToken(memberId.toString());
    }

    private Member findMemberById(final UUID id) {
        return memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);
    }
}
