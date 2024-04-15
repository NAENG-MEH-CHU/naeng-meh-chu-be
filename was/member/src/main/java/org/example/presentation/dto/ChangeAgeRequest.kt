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

    open fun getAge(): Age {
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