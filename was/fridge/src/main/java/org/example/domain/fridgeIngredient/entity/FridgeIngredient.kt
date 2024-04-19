package org.example.domain.fridgeIngredient.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.util.*

@Entity
@Table(name = "FridgeIngredient")
class FridgeIngredient(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
    @Column(nullable = false)
    val memberId: UUID,
    @Column(nullable = false)
    val ingredientId: Int,
    @Column(nullable = false)
    val name: String,
    @Column
    val expiresAt: LocalDate
) {

    constructor(memberId: UUID, ingredientId: Int, name:String, expiresAt: LocalDate) : this(null, memberId, ingredientId, name, expiresAt) {}

    protected constructor(): this(UUID.randomUUID(), 0,"",  LocalDate.now()){}
}