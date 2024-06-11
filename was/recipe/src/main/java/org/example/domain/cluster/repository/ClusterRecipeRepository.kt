package org.example.domain.cluster.repository

import org.example.domain.cluster.entity.ClusterRecipe
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ClusterRecipeRepository: JpaRepository<ClusterRecipe, UUID> {
    fun findByClusterId(clusterId: Int): List<ClusterRecipe>
}