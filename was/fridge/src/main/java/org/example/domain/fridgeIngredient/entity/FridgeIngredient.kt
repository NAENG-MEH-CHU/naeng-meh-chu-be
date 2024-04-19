package org.example.domain.fridgeIngredient.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*

@Entity
@Table(name = "FridgeIngredient")
class FridgeIngredient(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),
    @Column(nullable = false)
    val memberId: UUID,
    @Column(nullable = false)
    val ingredientId: Int,
    @Column(nullable = false)
    val name: String,
    @Column
    val expiresAt: LocalDate
) {

    constructor(memberId: UUID, ingredientId: Int, name:String, expiresAt: LocalDate) : this(UUID.randomUUID(), memberId, ingredientId, name, expiresAt) {}

    protected constructor(): this(UUID.randomUUID(), 0,"",  LocalDate.now()){}

    fun getDueDate(): Int { // D-day -n으로 나온다. 이미 지나면 +로 나온다.
        return -1 * ChronoUnit.DAYS.between(LocalDate.now(), expiresAt).toInt()
    }
}