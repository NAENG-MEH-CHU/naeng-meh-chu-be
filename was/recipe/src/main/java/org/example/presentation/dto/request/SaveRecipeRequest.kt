package org.example.presentation.dto.request

import org.example.domain.recipe.entity.Recipe

class SaveRecipeRequest(
    val name: String,
    val ingredients: String,
    val imgUrl : String,
    val link: String
) {

    constructor(): this("", "", "", "")

    fun mapToRecipe(): Recipe {
        return Recipe(ingredients, name, link, imgUrl)
    }
}