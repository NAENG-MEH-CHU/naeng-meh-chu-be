package org.example.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.util.UUID

@Entity
class Recipe(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID,
    @Column
    val ingredients: Long,
    @Column
    val name: String,
    @Column
    val recipeLink: String,
    @Column
    val thumbnail: String
) {
    constructor() : this(UUID.randomUUID(), 0, "", "", "") {}

    constructor(ingredients: Long, name: String, recipeLink: String, thumbnail: String): this(UUID.randomUUID(), ingredients, name, recipeLink, thumbnail)
}