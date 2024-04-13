package org.example.presentation

import jakarta.validation.Valid
import lombok.RequiredArgsConstructor
import org.example.application.MemberService
import org.example.domain.entity.Member
import org.example.presentation.dto.ChangeNicknameRequest
import org.example.support.JwtLogin
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
open class MemberController (private val memberService: MemberService){

    @PatchMapping("/nickname")
    open fun updateNickname(@JwtLogin member: Member, @RequestBody @Valid request: ChangeNicknameRequest): ResponseEntity<Unit> {
        memberService.updateNickname(request.getNickname(), member)
        return ResponseEntity(HttpStatus.OK)
    }
}