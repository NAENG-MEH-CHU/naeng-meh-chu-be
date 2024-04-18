package org.example.presentation.dto.response

import java.util.UUID

class SingleMyIngredientResponse(
    val id: UUID,
    val name: String,
    val ingredientId: Int,
    val dueDay: Int) {
}