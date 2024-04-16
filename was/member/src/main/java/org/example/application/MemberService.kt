package org.example.application

import lombok.RequiredArgsConstructor
import org.example.domain.entity.Member
import org.example.domain.entity.MemberReason
import org.example.domain.enums.Age
import org.example.domain.enums.Gender
import org.example.domain.enums.Gender.*
import org.example.domain.enums.UsingReason
import org.example.domain.repository.MemberReasonRepository
import org.example.domain.repository.MemberRepository
import org.example.exception.exceptions.*
import org.example.presentation.dto.request.ChangeAgeRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@RequiredArgsConstructor
open class MemberService(
    private val memberRepository: MemberRepository,
    private val memberReasonRepository: MemberReasonRepository
    ) {

    open fun findAgeOptions(): List<String> {
        return Age.entries.map { age -> age.type };
    }

    open fun findAllUsingReasons(): List<String> {
        return UsingReason.entries.map { reason -> reason.content }
    }

    @Transactional
    open fun addUsingReasons(reasons: List<String>, member: Member) {
        val reasonMap = getUsingReasonMap()
        val memberReasons = reasons.map { each ->
            MemberReason(member.id, parseReasonToUsingReason(reasonMap, each))
        }
        memberReasonRepository.saveAll(memberReasons)
    }

    @Transactional(readOnly = true)
    open fun findMyUsingReasons(member: Member): List<String> {
        return memberReasonRepository.findAllByMemberId(member.id).map {
            memberReason -> memberReason.reason.content
        }
    }

    @Transactional
    open fun deleteMemberReason(member: Member, reasonId: UUID) {
        val reason = memberReasonRepository.findById(reasonId).orElseThrow { MemberReasonNotFoundException() }
        if(reason.memberId.toString() != member.id.toString()) {
            throw MemberForbiddenException()
        }
        memberReasonRepository.delete(reason)
    }

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
    open fun updateAge(request: ChangeAgeRequest, member: Member) {
        val age = parseAge(request.getAge())
        member.updateAge(age)
        memberRepository.save(member)
    }

    @Transactional
    open fun deleteMember(member: Member) {
        if(memberRepository.existsById(member.id)) {
            memberRepository.delete(member)
            return
        }

        throw MemberNotFoundException()
    }

    private fun findGenderByInput(genderString: String): Gender {
        if(genderString == MALE.value) return MALE;
        if(genderString == FEMALE.value) return FEMALE;
        throw GenderNotValidException()
    }

    private fun getUsingReasonMap(): Map<String, UsingReason> {
        val map = HashMap<String, UsingReason>()
        UsingReason.entries.forEach { reason -> map[reason.content] = reason }
        return map
    }

    private fun parseReasonToUsingReason(reasonMap: Map<String, UsingReason>, reason: String): UsingReason {
        val usingReason =  reasonMap[reason]
        if(usingReason === null) {
            throw UsingReasonUnableException()
        }
        return usingReason
    }

    private fun parseAge(age: String): Age {
        return Age.entries.find { it.type == age } ?: throw AgeNotValidException()
    }
}