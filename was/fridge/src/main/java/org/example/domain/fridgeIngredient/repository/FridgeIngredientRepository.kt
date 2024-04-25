package org.example.domain.fridgeIngredient.repository

import org.example.domain.fridgeIngredient.entity.FridgeIngredient
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate
import java.util.UUID

interface FridgeIngredientRepository: JpaRepository<FridgeIngredient, UUID> {

    fun existsByIngredientIdAndMemberId(ingredientId: Int, memberId: UUID): Boolean

    fun findAllByMemberId(memberId: UUID): List<FridgeIngredient>

    @Query("select f from FridgeIngredient f where f.expiresAt <= ?1 order by f.expiresAt ASC")
    fun findFridgeIngredientsExpiresWithin(endDate: LocalDate): List<FridgeIngredient>
}