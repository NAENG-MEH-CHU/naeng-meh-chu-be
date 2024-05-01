package org.example.presentation.dto.response

import org.example.domain.memberRecipe.entity.MemberRecipe
import java.util.UUID

class MemberRecipeDataResponse(
    val memberRecipeId: UUID,
    val recipeId: UUID,
    val name: String,
    val thumbnail: String) {

    constructor(memberRecipe: MemberRecipe): this(memberRecipe.id, memberRecipe.recipeId, memberRecipe.name, memberRecipe.thumbnail)
}