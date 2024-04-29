package org.example.presentation.dto.request

import jakarta.validation.constraints.NotBlank

class DeleteIngredientRequest(
    @NotBlank var fridgeIngredientId: String
) {
}