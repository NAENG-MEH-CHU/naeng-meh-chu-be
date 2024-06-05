package org.example.domain.cluster.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.UUID

@Entity
class ClusterRecipe(
    @Id
    val id: UUID,
    val clusterId: Int
){

    constructor(): this(UUID.randomUUID(), 0)

    constructor(clusterId: Int): this(UUID.randomUUID(), clusterId)
}