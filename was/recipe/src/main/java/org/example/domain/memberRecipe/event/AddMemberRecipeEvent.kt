package org.example.domain.memberRecipe.event

import org.example.domain.enums.Age
import org.example.domain.enums.Gender
import org.example.domain.recipe.entity.Recipe
import java.util.UUID

class AddMemberRecipeEvent(
    val memberId: UUID,
    val memberAge: Age,
    val gender: Gender,
    val recipe: Recipe
) {
}