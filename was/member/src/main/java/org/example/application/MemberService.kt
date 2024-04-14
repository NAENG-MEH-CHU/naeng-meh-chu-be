package org.example.application

import lombok.RequiredArgsConstructor
import org.example.domain.entity.Member
import org.example.domain.enums.Gender
import org.example.domain.enums.Gender.*
import org.example.domain.repository.MemberRepository
import org.example.exception.exceptions.*
import org.example.presentation.dto.ChangeBirthRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

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
    open fun updateBirth(request: ChangeBirthRequest, member: Member) {
        member.updateAge(parseChangeBirthRequestToLocalDate(request))
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

    private fun parseChangeBirthRequestToLocalDate(request: ChangeBirthRequest): LocalDate {
        validateChangeBirthRequest(request)
        return LocalDate.of(request.year!!, request.month!!, request.day!!)
    }

    private fun validateChangeBirthRequest(request: ChangeBirthRequest) {
        if(isBirthInputInvalid(request.year))
            throw BirthNotValidException(getInvalidInputType(request.year), InvalidInputArea.YEAR)
        if(isBirthInputInvalid(request.month))
            throw BirthNotValidException(getInvalidInputType(request.month), InvalidInputArea.MONTH)
        if(isBirthInputInvalid(request.day))
            throw BirthNotValidException(getInvalidInputType(request.day), InvalidInputArea.DAY)
    }

    private fun isBirthInputInvalid(value: Int?): Boolean {
        return value == 0 || value === null
    }

    private fun getInvalidInputType(value: Int?): InputValueType {
        if(value === null) return InputValueType.NULL
        return InputValueType.ZERO
    }
}