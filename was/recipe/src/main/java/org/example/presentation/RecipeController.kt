package org.example.presentation

import org.example.application.memberRecipe.MemberRecipeService
import org.example.application.recipe.RecipeService
import org.example.domain.entity.Member
import org.example.presentation.dto.response.MemberRecipeDataListResponse
import org.example.presentation.dto.response.RecipeDataListResponse
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
    private val recipeService: RecipeService,
    private val memberRecipeService: MemberRecipeService
) {

    @GetMapping("")
    fun findAllRecipe(@JwtLogin member: Member): ResponseEntity<RecipeDataListResponse> {
        val recipes = recipeService.findAllRecipe()
        return ResponseEntity<RecipeDataListResponse>(RecipeDataListResponse(recipes), HttpStatus.OK)
    }

    @GetMapping("{id}")
    fun findRecipeById(@JwtLogin member: Member, @PathVariable("id") recipeId: UUID): ResponseEntity<RecipeResponse> {
        return ResponseEntity<RecipeResponse>(recipeService.findRecipeById(recipeId, member), HttpStatus.OK)
    }

    @GetMapping("/ingredients")
    fun findByMembersIngredients(@JwtLogin member: Member): ResponseEntity<RecipeDataListResponse> {
        val recipes = recipeService.findByMembersIngredients(member)
        return ResponseEntity<RecipeDataListResponse>(RecipeDataListResponse(recipes), HttpStatus.OK)
    }

    @GetMapping("/history")
    fun findMyRecipes(@JwtLogin member: Member): ResponseEntity<MemberRecipeDataListResponse> {
        val recipes = memberRecipeService.findMyRecipes(member)
        return ResponseEntity<MemberRecipeDataListResponse>(MemberRecipeDataListResponse(recipes), HttpStatus.OK)
    }

    @GetMapping("/recommend")
    fun findRecommendedRecipes(@JwtLogin member: Member): ResponseEntity<RecipeDataListResponse> {
        val recipes = recipeService.findRecommendableRecipes(member)
        return ResponseEntity<RecipeDataListResponse>(RecipeDataListResponse(recipes), HttpStatus.OK)
    }
}