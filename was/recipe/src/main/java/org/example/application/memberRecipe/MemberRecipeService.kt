package org.example.application.memberRecipe

import org.example.domain.entity.Member
import org.example.domain.memberRecipe.repository.MemberRecipeRepository
import org.example.presentation.dto.response.RecipeDataResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class MemberRecipeService(
    private val memberRecipeRepository: MemberRecipeRepository
) {

    @Transactional(readOnly = true)
    open fun findMyRecipes(member: Member): List<RecipeDataResponse> {
        val myRecipes = memberRecipeRepository.findAllByMemberId(member.id)
        return myRecipes.map { each -> RecipeDataResponse(each.recipeId, each.name, each.thumbnail) }
    }
}