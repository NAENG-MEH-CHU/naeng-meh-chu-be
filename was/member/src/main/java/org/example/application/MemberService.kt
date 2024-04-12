package org.example.application

import lombok.RequiredArgsConstructor
import org.example.domain.entity.Member
import org.example.domain.enums.Gender
import org.example.domain.enums.Gender.*
import org.example.domain.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@RequiredArgsConstructor
open class MemberService(private val memberRepository: MemberRepository) {

    @Transactional
    open fun updateNickname(nickname:String, member: Member) {
        member.updateNickname(nickname);
        memberRepository.save(member);
    }
}