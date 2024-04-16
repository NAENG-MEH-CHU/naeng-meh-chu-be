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
import org.example.presentation.dto.ChangeAgeRequest
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
        return UsingReason.entries.map { reason -> reason.getContent() }
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
            memberReason -> memberReason.reason.getContent()
        }
    }

    @Transactional
    open fun deleteMemberReason(member: Member, reasonId: UUID) {
        val reason = memberReasonRepository.findById(reasonId).orElseThrow { Error("못찾음") }
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
        UsingReason.entries.forEach { reason -> map[reason.getContent()] = reason }
        return map
    }

    private fun parseReasonToUsingReason(reasonMap: Map<String, UsingReason>, reason: String): UsingReason {
        val usingReason =  reasonMap[reason]
        if(usingReason === null) {
            throw Error("존재하지 않는 이용사유 요청")
        }
        return usingReason
    }

    private fun parseAge(age: String): Age {
        if (age == Age.TEEN.type) return Age.TEEN
        if (age == Age.TWENTIES.type) return Age.TWENTIES
        if (age == Age.THIRTIES.type) return Age.THIRTIES
        if (age == Age.FORTIES.type) return Age.FORTIES
        if (age == Age.FIFTIES.type) return Age.FIFTIES
        if (age == Age.SIXTIES.type) return Age.SIXTIES
        if (age == Age.SEVENTIES.type) return Age.SEVENTIES
        if (age == Age.EIGHTIES.type) return Age.EIGHTIES
        if (age == Age.NINETIES.type) return Age.NINETIES
        throw AgeNotValidException()
    }
}