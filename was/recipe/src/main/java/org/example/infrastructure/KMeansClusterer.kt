import org.apache.commons.math3.ml.clustering.CentroidCluster
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer
import org.apache.commons.math3.ml.distance.EuclideanDistance
import org.example.domain.memberRecipe.entity.MemberRecipe
import org.example.infrastructure.DataProcessor
import org.example.infrastructure.DoubleArrayWrapper
import java.util.*

class KMeansClusterer(
    private val dataProcessor: DataProcessor
) {

    fun clusterData(data: List<DoubleArray>?, numClusters: Int): List<CentroidCluster<DoubleArrayWrapper>> {
        val clusterableData = data?.map { DoubleArrayWrapper(it) } ?: emptyList()
        val clusterer = KMeansPlusPlusClusterer<DoubleArrayWrapper>(numClusters, 1000, EuclideanDistance())
        return clusterer.cluster(clusterableData)
    }

    fun findClusterIndexForRecipe(
        recipeId: UUID,
        clusters: List<CentroidCluster<DoubleArrayWrapper>>,
        recipes: List<MemberRecipe>
    ): Int {
        for (i in clusters.indices) {
            for (point in clusters[i].points) {
                val pointArray = point.point
                for (recipe in recipes) {
                    val recipeArray = dataProcessor.transformData(recipe)
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
        clusters: List<CentroidCluster<DoubleArrayWrapper>>,
        recipes: List<MemberRecipe>
    ): List<MemberRecipe> {
        val clusterIndex = findClusterIndexForRecipe(recipeId, clusters, recipes)
        if (clusterIndex == -1) {
            return emptyList()
        }

        val sameClusterRecipes: MutableList<MemberRecipe> = ArrayList()
        for (point in clusters[clusterIndex].points) {
            val pointArray = point.point
            for (recipe in recipes) {
                val recipeArray = dataProcessor.transformData(recipe)
                if (Arrays.equals(pointArray, recipeArray)) {
                    sameClusterRecipes.add(recipe)
                }
            }
        }

        return sameClusterRecipes
    }
}
