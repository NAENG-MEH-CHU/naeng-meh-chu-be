package org.example.presentation.dto.request

import jakarta.validation.constraints.NotNull

data class AddIngredientRequest(
    @field:NotNull(message = "재료의 id를 입력해주세요")
    val ingredientId: Int,
    @field:NotNull(message = "재료의 유통기한 년도를 입력해주세요")
    val year: Int,
    @field:NotNull(message = "재료의 유통기한 월을 입력해주세요")
    val month: Int,
    @field:NotNull(message = "재료의 유통기한 일을 입력해주세요")
    val day: Int
){

    constructor(): this(0, 0, 0, 0)
}