package org.example.application.recipe

import org.example.application.memberRecipe.MemberRecipeRecommender
import org.example.domain.entity.Member
import org.example.domain.memberRecipe.event.AddMemberRecipeEvent
import org.example.domain.recipe.entity.Recipe
import org.example.presentation.dto.response.RecipeResponse
import org.example.domain.recipe.repository.RecipeRepository
import org.example.exception.exceptions.RecipeNotFoundException
import org.example.presentation.dto.request.SaveRecipeRequest
import org.example.presentation.dto.response.RecipeDataResponse
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.math.min

@Service
open class RecipeService(
    private val recipeRepository: RecipeRepository,
    private val publisher: ApplicationEventPublisher,
    private val memberRecipeRecommender: MemberRecipeRecommender
) {

    private val recipeMap = HashMap<String, MutableList<Recipe>>();

    @Transactional
    open fun findRecipeById(recipeId: UUID, member: Member): RecipeResponse {
        val recipe = recipeRepository.findById(recipeId).orElseThrow { RecipeNotFoundException() }
        publisher.publishEvent(AddMemberRecipeEvent(member.id, member.age, member.gender, recipe))
        return RecipeResponse(recipe.recipeLink)
    }

    @Transactional
    open fun saveRecipes(recipes: List<SaveRecipeRequest>) {
        recipeRepository.saveAll(recipes.map { it.mapToRecipe() })
    }

    @Transactional(readOnly = true)
    open fun findByMembersIngredients(member: Member): List<RecipeDataResponse> {
        if(recipeMap.isEmpty()) {
            findRecipeData()
        }

        val result = mutableListOf<RecipeDataResponse>()

        recipeMap.keys
            .filter { isIngredientsContained(it, member.ingredients) }
            .forEach { result.addAll(recipeMap[it]!!.map { RecipeDataResponse(it) }) }

        return result
    }

    @Transactional(readOnly = true)
    open fun findAllRecipe(): List<RecipeDataResponse> {
        return recipeRepository.findAll().map{ RecipeDataResponse(it) }
    }

    @Transactional(readOnly = true)
    open fun findRecommendableRecipes(member: Member): List<RecipeDataResponse> {
        val recipes = recipeRepository.findAllById(memberRecipeRecommender.findRecommendingRecipeIdsByMemberId(member.id))
        return recipes.map { RecipeDataResponse(it) }
    }

    @Transactional(readOnly = true)
    open fun findRecipeData() {
        recipeRepository.findAll().forEach{ addRecipeToMap(it) }
    }

    private fun addRecipeToMap(recipe: Recipe) {
        if(recipeMap[recipe.ingredients] == null) {
            recipeMap[recipe.ingredients] = mutableListOf(recipe)
            return
        }

        recipeMap[recipe.ingredients]!!.add(recipe)
    }

    private fun isIngredientsContained(recipeIngredients : String, memberIngredients: String): Boolean {
        if(recipeIngredients.length > memberIngredients.length) return false; // 길다는 것. 재료가 더 많다는 것.
        for(index in recipeIngredients.indices) {
            if(recipeIngredients[index] != '1') continue
            if(memberIngredients[index] != '1') return false
        }
        return true
    }
}