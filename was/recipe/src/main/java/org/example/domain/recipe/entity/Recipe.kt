package org.example.domain.recipe.entity

import jakarta.persistence.*
import java.util.*

@Entity
class Recipe(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID,
    @Lob
    @Column(columnDefinition = "TEXT")
    val ingredients: String,
    @Column
    val name: String,
    @Column
    val recipeLink: String,
    @Column
    val thumbnail: String
) {
    constructor() : this(UUID.randomUUID(), "0", "", "", "") {}

    constructor(ingredients: String, name: String, recipeLink: String, thumbnail: String): this(UUID.randomUUID(), ingredients, name, recipeLink, thumbnail)
}