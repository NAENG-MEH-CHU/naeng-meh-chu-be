package org.example.domain.recipe.repository

import org.example.domain.recipe.entity.Recipe
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface RecipeRepository: JpaRepository<Recipe, UUID> {

    @Query(value = "SELECT * FROM recipe r WHERE (r.ingredients & :memberIngredients) = r.ingredients or (r.ingredients & :memberIngredients) = :memberIngredients", nativeQuery = true)
    fun findAllByIngredients(memberIngredients: Long): List<Recipe>
}