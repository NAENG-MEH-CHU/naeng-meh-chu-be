package org.example.domain.fridgeIngredient.repository

import org.example.domain.fridgeIngredient.entity.FridgeIngredient
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface FridgeIngredientRepository: JpaRepository<FridgeIngredient, UUID> {

    fun existsByIngredientIdAndMemberId(ingredientId: Int, memberId: UUID): Boolean
}