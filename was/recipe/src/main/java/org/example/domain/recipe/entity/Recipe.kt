package org.example.domain.recipe.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(indexes = [Index(name = "idx_ingredients", columnList = "ingredients")])
class Recipe(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID,
    @Lob
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