package org.example.presentation.dto.response

import org.example.domain.fridgeIngredient.entity.FridgeIngredient
import java.util.UUID

class SingleMyIngredientResponse(
    val id: UUID,
    val name: String,
    val ingredientId: Int,
    val dueDay: Int) {

    constructor(fridgeIngredient: FridgeIngredient): this(fridgeIngredient.id, fridgeIngredient.name, fridgeIngredient.ingredientId, fridgeIngredient.getDueDate())
}