package org.example.application;

import lombok.AllArgsConstructor;
import org.example.domain.entity.Member;
import org.example.domain.repository.MemberRepository;
import org.example.exception.exceptions.MemberNotFoundException;
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
    public Member findMemberByJwtPayload(final String jwtPayload) {
        String id = jwtTokenProvider.getPayload(jwtPayload);
        return memberRepository.findById(UUID.fromString(id))
                .orElseThrow(MemberNotFoundException::new);
    }
}
