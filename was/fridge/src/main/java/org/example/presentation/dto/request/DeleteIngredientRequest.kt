package org.example.presentation.dto.request

import jakarta.validation.constraints.NotBlank

class DeleteIngredientRequest(
    @NotBlank val fridgeIngredientId: String
) {

    constructor(): this("")
}