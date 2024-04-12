package org.example.application;

import lombok.RequiredArgsConstructor;
import org.example.domain.entity.Member;
import org.example.domain.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;


    @Transactional
    public void updateNickname(final String nickname, Member member) {
        member.updateNickname(nickname);
        memberRepository.save(member);
    }
}
