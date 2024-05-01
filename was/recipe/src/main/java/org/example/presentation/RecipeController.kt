package org.example.presentation

import org.example.application.recipe.RecipeService
import org.example.domain.entity.Member
import org.example.presentation.dto.response.RecipeResponse
import org.example.support.JwtLogin
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/recipe")
class RecipeController(
    private val recipeService: RecipeService
) {

    @GetMapping("{id}")
    fun findRecipeById(@JwtLogin member: Member, @PathVariable("id") recipeId: UUID): ResponseEntity<RecipeResponse> {
        return ResponseEntity<RecipeResponse>(recipeService.findRecipeById(recipeId, member), HttpStatus.OK)
    }
}