package org.example.config

import KMeansClusterer
import org.example.infrastructure.DataProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class ClusterConfig {

    @Bean
    open fun dataProcessor(): DataProcessor {
        return DataProcessor()
    }

    @Bean
    open fun cluster(): KMeansClusterer {
        return KMeansClusterer(dataProcessor())
    }
}