package org.example.application

import lombok.RequiredArgsConstructor
import org.example.domain.entity.Member
import org.example.domain.enums.Gender
import org.example.domain.enums.Gender.*
import org.example.domain.repository.MemberRepository
import org.example.exception.exceptions.MemberNotFoundException
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

    @Transactional
    open fun updateGender(genderString: String, member: Member) {
        member.updateGender(findGenderByInput(genderString));
        memberRepository.save(member);
    }

    @Transactional
    open fun deleteMember(member: Member) {
        memberRepository.findById(member.id).orElseThrow{MemberNotFoundException()}
        memberRepository.delete(member)
    }

    private fun findGenderByInput(genderString: String): Gender {
        if(genderString == MALE.value) return MALE;
        return FEMALE;
    }
}