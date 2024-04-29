package org.example.domain.ingredient.repository

import org.example.domain.ingredient.entity.Ingredient
import org.springframework.data.jpa.repository.JpaRepository

interface IngredientRepository: JpaRepository<Ingredient, Int> {
}