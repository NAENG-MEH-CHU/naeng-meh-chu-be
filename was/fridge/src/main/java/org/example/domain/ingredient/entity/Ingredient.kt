package org.example.domain.ingredient.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "Ingredient")
class Ingredient(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int = 0,

    @Column
    val name: String
){
    constructor(): this(0, ""){}
}