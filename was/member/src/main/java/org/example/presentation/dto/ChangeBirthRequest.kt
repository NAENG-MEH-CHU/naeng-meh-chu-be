package org.example.presentation.dto

import jakarta.validation.constraints.NotNull

open class ChangeBirthRequest {

    @NotNull(message = "출생 년도를 입력해주세요") var year: Int? = null
    @NotNull(message = "출생 월을 입력해주세요") var month: Int? = null
    @NotNull(message = "출생 일을 입력해주세요") var day: Int? = null

    constructor(year: Int, month: Int, day: Int) {
        this.year = year
        this.month = month
        this.day = day
    }

    constructor(){}
}