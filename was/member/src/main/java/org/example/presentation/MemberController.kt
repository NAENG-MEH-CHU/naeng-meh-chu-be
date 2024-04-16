package org.example.presentation

import jakarta.validation.Valid
import lombok.RequiredArgsConstructor
import org.example.application.MemberService
import org.example.domain.entity.Member
import org.example.presentation.dto.request.ChangeAgeRequest
import org.example.presentation.dto.request.ChangeGenderRequest
import org.example.presentation.dto.request.ChangeNicknameRequest
import org.example.presentation.dto.response.AgeListResponse
import org.example.presentation.dto.response.MemberResponse
import org.example.support.JwtLogin
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
open class MemberController (private val memberService: MemberService){

    @GetMapping("/age")
    open fun findAgeOptions(@JwtLogin member: Member): ResponseEntity<AgeListResponse> {
        val response = AgeListResponse(memberService.findAgeOptions())
        return ResponseEntity<AgeListResponse>(response, HttpStatus.OK)
    }

    @PatchMapping("/age")
    open fun updateAge(@JwtLogin member: Member, @RequestBody @Valid request: ChangeAgeRequest): ResponseEntity<Unit> {
        memberService.updateAge(request, member)
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/me")
    open fun findMemberData(@JwtLogin member: Member): ResponseEntity<MemberResponse> {
        return ResponseEntity<MemberResponse>(MemberResponse(member), HttpStatus.OK)
    }

    @PatchMapping("/nickname")
    open fun updateNickname(@JwtLogin member: Member, @RequestBody @Valid request: ChangeNicknameRequest): ResponseEntity<Unit> {
        memberService.updateNickname(request.getNickname(), member)
        return ResponseEntity(HttpStatus.OK)
    }

    @PatchMapping("/gender")
    open fun updateGender(@JwtLogin member: Member, @RequestBody @Valid request: ChangeGenderRequest): ResponseEntity<Unit> {
        memberService.updateGender(request.getGender(), member)
        return ResponseEntity(HttpStatus.OK)
    }

    @DeleteMapping("")
    open fun deleteMember(@JwtLogin member: Member): ResponseEntity<Unit> {
        memberService.deleteMember(member)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}