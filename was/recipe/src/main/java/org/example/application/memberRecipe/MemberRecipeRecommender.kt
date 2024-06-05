package org.example.application.memberRecipe

import KMeansClusterer
import org.example.domain.cluster.repository.ClusterRecipeRepository
import org.example.domain.memberRecipe.entity.MemberRecipe
import org.example.domain.memberRecipe.event.AddMemberRecipeEvent
import org.example.domain.memberRecipe.repository.MemberRecipeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionalEventListener
import java.util.UUID

@Service
open class MemberRecipeRecommender(
    private val memberRecipeRepository: MemberRecipeRepository,
    private val clusterRecipeRepository: ClusterRecipeRepository,
    private val clusterer: KMeansClusterer
) {

    private val BATCH_SIZE = 100
    private var batchCount = 0;

    @TransactionalEventListener(AddMemberRecipeEvent::class)
    fun countBatch(event: AddMemberRecipeEvent) {
        batchCount++

        if(batchCount == BATCH_SIZE) {
            updateCluster()
            batchCount = 0
        }
    }

    @Transactional(readOnly = true)
    open fun findRecommendingRecipeIdsByMemberId(memberId: UUID): List<UUID> {
        val memberRecipeIds = findMemberRecipes(memberId)
        val result = mutableListOf<UUID>()

        memberRecipeIds.forEach { result.addAll(getRecipesInSameCluster(it.id)) }
        return result
    }

    private fun findMemberRecipes(memberId: UUID): List<MemberRecipe> {
        return memberRecipeRepository.findAllByMemberId(memberId)
    }

    private fun getRecipesInSameCluster(memberRecipeId: UUID): List<UUID> {
        val clusteredRecipe = clusterRecipeRepository.findById(memberRecipeId).orElse(null) ?: return emptyList()
        val clusterId = clusteredRecipe.clusterId
        val clusteredRecipes = clusterRecipeRepository.findByClusterId(clusterId)
        val recipeIds = clusteredRecipes.map { it.id }
        val memberRecipeInCluster = memberRecipeRepository.findAllById(recipeIds)
        return memberRecipeInCluster.map { it.recipeId }
    }

    open fun updateCluster() {
        clusterer.initializeClustersAsync(memberRecipeRepository.findAll(), 1000)
    }
}