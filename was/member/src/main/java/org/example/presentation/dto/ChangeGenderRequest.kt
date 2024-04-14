package org.example.presentation.dto

import jakarta.validation.constraints.NotBlank

open class ChangeGenderRequest {

    @NotBlank(message = "성별을 입력해주세요") private lateinit var gender: String

    constructor(gender: String) {
        this.gender = gender
    }

    constructor(){}

    open fun getGender(): String {
        return gender
    }
}