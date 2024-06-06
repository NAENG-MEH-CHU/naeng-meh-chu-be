package org.example.presentation.dto.request

class SaveRecipesRequest(
    val recipeRequests: List<SaveRecipeRequest>
) {

    constructor(): this(listOf())
}