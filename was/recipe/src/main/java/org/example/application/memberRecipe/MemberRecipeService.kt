package org.example.application.memberRecipe

import org.example.domain.entity.Member
import org.example.domain.memberRecipe.repository.MemberRecipeRepository
import org.example.presentation.dto.response.MemberRecipeDataResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class MemberRecipeService(
    private val memberRecipeRepository: MemberRecipeRepository
) {

    @Transactional(readOnly = true)
    open fun findMyRecipes(member: Member): List<MemberRecipeDataResponse> {
        val myRecipes = memberRecipeRepository.findAllByMemberId(member.id)
        return myRecipes.map { each -> MemberRecipeDataResponse(each) }
    }
}