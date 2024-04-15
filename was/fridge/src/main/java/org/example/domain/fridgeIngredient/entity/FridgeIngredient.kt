package org.example.domain.fridgeIngredient.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.util.*

@Entity
@Table(name = "FridgeIngredient")
open class FridgeIngredient(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private var id: UUID? = null,
    @Column(nullable = false)
    private var memberId: UUID,
    @Column(nullable = false)
    private var ingredientId: Int,
    @Column
    private var expiresAt: LocalDate
) {

    constructor(memberId: UUID, ingredientId: Int, expiresAt: LocalDate) : this(null, memberId, ingredientId, expiresAt) {
        this.memberId = memberId
        this.ingredientId = ingredientId
        this.expiresAt = expiresAt
    }

    protected constructor(): this(null, UUID.randomUUID(), 0, LocalDate.now()){}
}