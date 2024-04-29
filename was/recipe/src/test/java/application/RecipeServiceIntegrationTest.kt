package application

import org.example.RecipeApplication
import org.example.application.recipe.RecipeService
import org.example.domain.recipe.repository.RecipeRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationEventPublisher

@SpringBootTest(classes = [RecipeApplication::class])
@AutoConfigureMockMvc
class RecipeServiceIntegrationTest(
    @Autowired
    private val recipeRepository: RecipeRepository,
    @Autowired
    private val publisher: ApplicationEventPublisher,
    @Autowired
    private val recipeService: RecipeService
) {

    @DisplayName("레시피 단건을 조회한다")
    @Test
    fun `레시피 단건 조회`() {

    }
}