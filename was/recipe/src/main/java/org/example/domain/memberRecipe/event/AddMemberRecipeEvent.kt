package org.example.domain.memberRecipe.event

import org.example.domain.enums.Age
import org.example.domain.enums.Gender
import java.util.UUID

class AddMemberRecipeEvent(
    val recipeId: UUID,
    val memberId: UUID,
    val memberAge: Age,
    val gender: Gender,
) {
}