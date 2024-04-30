package org.example.domain.recipe.repository

import org.example.domain.recipe.entity.Recipe
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RecipeRepository: JpaRepository<Recipe, UUID> {
}