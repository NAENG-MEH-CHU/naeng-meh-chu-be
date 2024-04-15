package org.example.presentation.dto

import jakarta.validation.constraints.NotBlank

class DeleteIngredientRequest(
    @NotBlank var fridgeIngredientId: String
) {
}