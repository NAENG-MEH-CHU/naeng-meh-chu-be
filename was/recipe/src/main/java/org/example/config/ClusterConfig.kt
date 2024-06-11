package org.example.config

import KMeansClusterer
import org.example.domain.cluster.repository.ClusterRecipeRepository
import org.example.infrastructure.DataProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class ClusterConfig(
    private val clusterRecipeRepository: ClusterRecipeRepository
) {

    @Bean
    open fun dataProcessor(): DataProcessor {
        return DataProcessor()
    }

    @Bean
    open fun cluster(): KMeansClusterer {
        return KMeansClusterer(dataProcessor(), clusterRecipeRepository)
    }
}