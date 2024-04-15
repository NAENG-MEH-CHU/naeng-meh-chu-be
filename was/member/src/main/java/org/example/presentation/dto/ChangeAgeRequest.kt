package org.example.presentation.dto

import jakarta.validation.constraints.NotBlank
import org.example.domain.enums.Age
import org.example.exception.exceptions.AgeNotValidException

open class ChangeAgeRequest {

    @NotBlank(message = "나이대를 입력해주세요") private lateinit var age: String

    constructor(age: String) {
        this.age = age
    }

    constructor(){}

    open fun getAge(): String {
        return age
    }
}