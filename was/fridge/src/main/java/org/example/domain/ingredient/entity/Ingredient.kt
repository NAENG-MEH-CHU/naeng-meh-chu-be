package org.example.domain.ingredient.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "Ingredient")
open class Ingredient{

    constructor(id: Int?, name: String) {
        this.id = id
        this.name = name
    }

    constructor(){}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private var id: Int? = null

    @Column
    private lateinit var name: String
}