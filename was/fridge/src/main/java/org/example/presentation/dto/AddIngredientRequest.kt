package org.example.presentation.dto

import jakarta.validation.constraints.NotNull

open class AddIngredientRequest(
    @field:NotNull var ingredientId: Int = 0,
    @field:NotNull var year: Int = 0,
    @field:NotNull var month: Int = 0,
    @field:NotNull var day: Int = 0
){
}