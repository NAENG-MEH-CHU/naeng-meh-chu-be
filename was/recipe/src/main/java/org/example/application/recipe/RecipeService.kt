package org.example.application.recipe

import org.example.domain.entity.Member
import org.example.domain.memberRecipe.event.AddMemberRecipeEvent
import org.example.presentation.dto.response.RecipeResponse
import org.example.domain.recipe.repository.RecipeRepository
import org.example.exception.exceptions.RecipeNotFoundException
import org.example.presentation.dto.response.RecipeDataResponse
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
open class RecipeService(
    private val recipeRepository: RecipeRepository,
    private val publisher: ApplicationEventPublisher
) {

    @Transactional(readOnly = true)
    open fun findRecipeById(recipeId: UUID, member: Member): RecipeResponse {
        val recipe = recipeRepository.findById(recipeId).orElseThrow { RecipeNotFoundException() }
        publisher.publishEvent(AddMemberRecipeEvent(recipeId, member.id, member.age, member.gender))
        return RecipeResponse(recipe.recipeLink)
    }

    @Transactional(readOnly = true)
    open fun findByMembersIngredients(member: Member): List<RecipeDataResponse> {
        return recipeRepository.findAllByIngredients(member.ingredients).map { each -> RecipeDataResponse(each) }
    }
}