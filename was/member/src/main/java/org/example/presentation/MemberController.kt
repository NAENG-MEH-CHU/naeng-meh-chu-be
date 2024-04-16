package org.example.presentation

import jakarta.validation.Valid
import lombok.RequiredArgsConstructor
import org.example.application.MemberService
import org.example.domain.entity.Member
import org.example.presentation.dto.request.AddUsingReasonRequest
import org.example.presentation.dto.request.ChangeAgeRequest
import org.example.presentation.dto.request.ChangeGenderRequest
import org.example.presentation.dto.request.ChangeNicknameRequest
import org.example.presentation.dto.response.AgeListResponse
import org.example.presentation.dto.response.MemberResponse
import org.example.presentation.dto.response.UsingReasonsResponse
import org.example.support.JwtLogin
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
class MemberController (private val memberService: MemberService){

    @GetMapping("/age")
    fun findAgeOptions(@JwtLogin member: Member): ResponseEntity<AgeListResponse> {
        val response = AgeListResponse(memberService.findAgeOptions())
        return ResponseEntity<AgeListResponse>(response, HttpStatus.OK)
    }

    @GetMapping("/reasons")
    fun findAllUsingReasons(@JwtLogin member: Member): ResponseEntity<UsingReasonsResponse> {
        val response = UsingReasonsResponse(memberService.findAllUsingReasons())
        return ResponseEntity<UsingReasonsResponse>(response, HttpStatus.OK)
    }

    @PostMapping("/reasons")
    fun addUsingReasons(@JwtLogin member: Member, @RequestBody @Valid addUsingReasonRequest: AddUsingReasonRequest): ResponseEntity<Unit> {
        memberService.addUsingReasons(addUsingReasonRequest.reasons!!, member)
        return ResponseEntity<Unit>(HttpStatus.CREATED)
    }

    @GetMapping("/reasons/me")
    fun findMyUsingReasons(@JwtLogin member: Member): ResponseEntity<UsingReasonsResponse> {
        val responseData = memberService.findMyUsingReasons(member)
        return ResponseEntity<UsingReasonsResponse>(UsingReasonsResponse(responseData), HttpStatus.OK)
    }

    @DeleteMapping("/reasons")
    fun deleteMemberReason(@JwtLogin member: Member, @RequestParam(name = "id") id: String): ResponseEntity<Unit> {
        memberService.deleteMemberReason(member, UUID.fromString(id))
        return ResponseEntity<Unit>(HttpStatus.NO_CONTENT)
    }

    @PatchMapping("/age")
    fun updateAge(@JwtLogin member: Member, @RequestBody @Valid request: ChangeAgeRequest): ResponseEntity<Unit> {
        memberService.updateAge(request, member)
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/me")
    fun findMemberData(@JwtLogin member: Member): ResponseEntity<MemberResponse> {
        return ResponseEntity<MemberResponse>(MemberResponse(member), HttpStatus.OK)
    }

    @PatchMapping("/nickname")
    fun updateNickname(@JwtLogin member: Member, @RequestBody @Valid request: ChangeNicknameRequest): ResponseEntity<Unit> {
        memberService.updateNickname(request.getNickname(), member)
        return ResponseEntity(HttpStatus.OK)
    }

    @PatchMapping("/gender")
    fun updateGender(@JwtLogin member: Member, @RequestBody @Valid request: ChangeGenderRequest): ResponseEntity<Unit> {
        memberService.updateGender(request.getGender(), member)
        return ResponseEntity(HttpStatus.OK)
    }

    @DeleteMapping("")
    fun deleteMember(@JwtLogin member: Member): ResponseEntity<Unit> {
        memberService.deleteMember(member)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}