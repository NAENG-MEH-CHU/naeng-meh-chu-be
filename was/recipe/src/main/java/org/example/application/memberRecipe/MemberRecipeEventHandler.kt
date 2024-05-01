package org.example.application.memberRecipe

import org.example.domain.memberRecipe.entity.MemberRecipe
import org.example.domain.memberRecipe.event.AddMemberRecipeEvent
import org.example.domain.memberRecipe.event.DeleteMemberRecipeEvent
import org.example.domain.memberRecipe.repository.MemberRecipeRepository
import org.example.exception.exceptions.MemberRecipeAlreadyDeletedException
import org.example.exception.exceptions.MemberRecipeNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.event.TransactionalEventListener

@Service
class MemberRecipeEventHandler(
    private val memberRecipeRepository: MemberRecipeRepository
) {

    @TransactionalEventListener(AddMemberRecipeEvent::class)
    fun addMemberRecipe(event: AddMemberRecipeEvent) {
        val memberRecipe = MemberRecipe(event.recipeId,
            event.memberId,
            event.memberAge,
            event.gender,
            event.recipe)
        memberRecipeRepository.save(memberRecipe)
    }

    @TransactionalEventListener(DeleteMemberRecipeEvent::class)
    fun deleteMemberRecipe(event: DeleteMemberRecipeEvent) {
        val memberRecipe = memberRecipeRepository.findById(event.memberRecipeId)
            .orElseThrow { MemberRecipeNotFoundException() }

        if(memberRecipe.isAlreadyDeleted()) {
            throw MemberRecipeAlreadyDeletedException()
        }

        memberRecipe.deleteMemberRecipe()
        memberRecipeRepository.save(memberRecipe)
    }
}