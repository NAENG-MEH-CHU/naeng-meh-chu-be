package org.example.presentation.dto

import jakarta.validation.constraints.NotBlank
open class ChangeNicknameRequest {

    @NotBlank(message = "수정할 닉네임을 입력해주세요")
    private lateinit var nickname: String

    constructor(nickname: String) {
        this.nickname = nickname
    }

    constructor() {}

    open fun getNickname(): String {
        return nickname
    }
}