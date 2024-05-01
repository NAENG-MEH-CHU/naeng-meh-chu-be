package org.example.presentation.dto.response

import org.example.domain.recipe.entity.Recipe
import java.util.UUID

class RecipeDataResponse(
    val id: UUID,
    val name: String,
    val thumbnail: String,
) {

    constructor(recipe: Recipe): this(recipe.id, recipe.name, recipe.thumbnail){}
}