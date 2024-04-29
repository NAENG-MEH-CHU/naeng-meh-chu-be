package org.example.domain.memberRecipe.event

import java.util.UUID

class DeleteMemberRecipeEvent(
    val memberRecipeId: UUID,
    val memberId: UUID
) {
}