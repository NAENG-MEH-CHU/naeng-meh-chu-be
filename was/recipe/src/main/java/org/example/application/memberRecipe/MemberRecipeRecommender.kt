package org.example.application.memberRecipe

import KMeansClusterer
import org.example.domain.memberRecipe.repository.MemberRecipeRepository
import org.springframework.stereotype.Service

@Service
class MemberRecipeRecommender(
    private val memberRecipeRepository: MemberRecipeRepository,
    private val clusterer: KMeansClusterer
) {


}