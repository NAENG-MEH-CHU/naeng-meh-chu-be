import jakarta.annotation.PostConstruct
import org.apache.commons.math3.ml.clustering.CentroidCluster
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer
import org.apache.commons.math3.ml.distance.EuclideanDistance
import org.example.domain.cluster.entity.ClusterRecipe
import org.example.domain.cluster.repository.ClusterRecipeRepository
import org.example.domain.memberRecipe.entity.MemberRecipe
import org.example.infrastructure.DataProcessor
import org.example.infrastructure.DoubleArrayWrapper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.util.*

@Component
open class KMeansClusterer(
    private val dataProcessor: DataProcessor,
    private val clusteredRecipeRepository: ClusterRecipeRepository
) {

    private var clusters: List<CentroidCluster<DoubleArrayWrapper>> = emptyList()
    private var recipeMap: Map<UUID, DoubleArray> = emptyMap()

    @Async
    open fun initializeClustersAsync(data: List<MemberRecipe>, numClusters: Int) {
        clusters = clusterData(data, numClusters)
        recipeMap = data.associateBy({ it.recipeId }, { dataProcessor.transformData(it) })

        // 클러스터링 결과를 저장
        saveClusterResults()
    }

    private fun saveClusterResults() {
        clusteredRecipeRepository.deleteAll()
        val clusteredRecipes = mutableListOf<ClusterRecipe>()
        for (i in clusters.indices) {
            for (point in clusters[i].points) {
                val pointArray = point.point
                val recipeId = recipeMap.entries.find { Arrays.equals(it.value, pointArray) }?.key
                recipeId?.let { clusteredRecipes.add(ClusterRecipe(it, i)) }
            }
        }
        clusteredRecipeRepository.saveAll(clusteredRecipes)
    }

    fun clusterData(data: List<MemberRecipe>, numClusters: Int): List<CentroidCluster<DoubleArrayWrapper>> {
        val clusterableData = data.map { DoubleArrayWrapper(dataProcessor.transformData(it)) } ?: emptyList()
        val clusterer = KMeansPlusPlusClusterer<DoubleArrayWrapper>(numClusters, 1000, EuclideanDistance())
        return clusterer.cluster(clusterableData)
    }
}
