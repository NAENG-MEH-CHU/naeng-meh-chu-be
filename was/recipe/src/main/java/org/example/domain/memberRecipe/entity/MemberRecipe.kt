package org.example.domain.memberRecipe.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.example.domain.enums.Age
import org.example.domain.enums.Gender
import org.example.domain.recipe.entity.Recipe
import java.time.LocalDateTime
import java.util.UUID

@Entity
class MemberRecipe(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID,
    @Column
    val recipeId: UUID,
    @Column
    val memberId: UUID,
    @Column
    val createdAt: LocalDateTime,
    @Column
    val memberAge: Age,
    @Column
    val gender: Gender,
    @Column
    var deleted: Boolean,
    @Column
    var name: String,
    @Column
    var thumbnail: String
) {
    constructor() : this(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now(), Age.THIRTIES, Gender.MALE, false, "", "") {}

    constructor(recipeId: UUID, memberId: UUID, memberAge: Age, gender: Gender, recipe: Recipe): this(UUID.randomUUID(), recipeId, memberId, LocalDateTime.now(), memberAge, gender, false, recipe.name, recipe.thumbnail)

    fun deleteMemberRecipe() {
        deleted = true
    }

    fun isAlreadyDeleted(): Boolean {
        return deleted
    }
}