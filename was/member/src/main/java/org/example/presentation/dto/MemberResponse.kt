package org.example.presentation.dto

import org.example.domain.entity.Member

class MemberResponse{

    private var nickname: String
    private var age: String
    private var gender: String

    constructor(member: Member) {
        this.nickname = member.nickname
        this.age = findAge(member)
        this.gender = findGender(member)
    }

    private fun findAge(member: Member): String {
        if(member.age === null ) return "나이대가 설정되지 않았습니다"
        return member.age.type
    }

    private fun findGender(member: Member): String {
        if(member.gender === null ) return "성별이 설정되지 않았습니다"
        return member.gender.value
    }
}