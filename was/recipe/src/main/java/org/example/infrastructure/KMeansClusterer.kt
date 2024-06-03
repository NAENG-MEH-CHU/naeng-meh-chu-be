package org.example.infrastructure

import org.apache.commons.math3.ml.clustering.*
import org.apache.commons.math3.ml.distance.EuclideanDistance
import org.example.domain.memberRecipe.entity.MemberRecipe
import org.springframework.stereotype.Service
import java.util.*


@Service
class KMeansClusterer(
) {
    fun clusterData(data: List<DoubleArray>?, numClusters: Int): List<CentroidCluster<DoubleArray>> {
        val clusterer: KMeansPlusPlusClusterer<DoubleArray> =
            KMeansPlusPlusClusterer(numClusters, 1000, EuclideanDistance())
        return clusterer.cluster(data)
    }

    fun findClusterIndexForRecipe(
        recipeId: UUID,
        clusters: List<CentroidCluster<DoubleArray>>,
        recipes: List<MemberRecipe>
    ): Int {
        for (i in clusters.indices) {
            for (point in clusters[i].getPoints()) {
                val pointArray = point.point
                for (recipe in recipes) {
                    val recipeArray = doubleArrayOf(ageToDouble(recipe.memberAge), genderToDouble(recipe.gender))
                    if (Arrays.equals(pointArray, recipeArray) && recipe.recipeId == recipeId) {
                        return i
                    }
                }
            }
        }
        return -1 // 클러스터를 찾지 못한 경우
    }

    fun getRecipesInSameCluster(
        recipeId: UUID,
        clusters: List<CentroidCluster<DoubleArray>>,
        recipes: List<MemberRecipe>
    ): List<MemberRecipe> {
        val clusterIndex = findClusterIndexForRecipe(recipeId, clusters, recipes)
        if (clusterIndex == -1) {
            return Collections.emptyList()
        }

        val sameClusterRecipes: MutableList<MemberRecipe> = ArrayList()
        for (point in clusters[clusterIndex].getPoints()) {
            val pointArray = point.point
            for (recipe in recipes) {
                val recipeArray = doubleArrayOf(ageToDouble(recipe.memberAge), genderToDouble(recipe.gender))
                if (Arrays.equals(pointArray, recipeArray)) {
                    sameClusterRecipes.add(recipe)
                }
            }
        }
        return sameClusterRecipes
    }
}