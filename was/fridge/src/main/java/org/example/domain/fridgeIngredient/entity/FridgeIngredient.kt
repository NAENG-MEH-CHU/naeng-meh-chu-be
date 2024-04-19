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
    @Column
    val expiresAt: LocalDate
) {

    constructor(memberId: UUID, ingredientId: Int, expiresAt: LocalDate) : this(null, memberId, ingredientId, expiresAt) {}

    constructor(): this(null, UUID.randomUUID(), 0, LocalDate.now()){}
}