package org.example.domain.recipe.repository

import org.example.domain.recipe.entity.Recipe
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*


interface RecipeRepository: JpaRepository<Recipe, UUID> {

    @Query(value = "SELECT * FROM recipe r WHERE (LENGTH(r.ingredients) >= LENGTH(:memberIngredients) AND SUBSTRING(r.ingredients, 1, LENGTH(:memberIngredients)) = :memberIngredients) OR (LENGTH(r.ingredients) < LENGTH(:memberIngredients) AND SUBSTRING(:memberIngredients, 1, LENGTH(r.ingredients)) = r.ingredients)", nativeQuery = true)
    fun findAllByIngredients(memberIngredients: String): List<Recipe>
}