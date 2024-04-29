package application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.example.RecipeApplication
import org.example.application.recipe.RecipeService
import org.example.domain.entity.Member
import org.example.domain.enums.Gender
import org.example.domain.recipe.entity.Recipe
import org.example.domain.recipe.repository.RecipeRepository
import org.example.domain.repository.MemberRepository
import org.example.exception.exceptions.RecipeNotFoundException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationEventPublisher
import java.util.*

@SpringBootTest(classes = [RecipeApplication::class])
@AutoConfigureMockMvc
class RecipeServiceIntegrationTest(
    @Autowired
    private val recipeRepository: RecipeRepository,
    @Autowired
    private val publisher: ApplicationEventPublisher,
    @Autowired
    private val recipeService: RecipeService,
    @Autowired
    private val memberRepository: MemberRepository
) {

    private lateinit var member: Member

    @BeforeEach
    fun init() {
        member = Member.builder()
            .nickname("before")
            .age(null)
            .gender(Gender.MALE)
            .email("test@test.com")
            .ingredients(0)
            .build()
        member = memberRepository.save(member);
    }

    @DisplayName("레시피 단건을 조회한다")
    @Test
    fun `레시피 단건 조회`() {
        // given
        val recipe = recipeRepository.save(Recipe(0, "tester", "link", "thumbnail"))

        // when
        val result = recipeService.findRecipeById(recipe.id)

        // then
        result.recipeLink shouldBe recipe.recipeLink
    }

    @DisplayName("존재하지 않는 아이디라면 레시피 조회 실패")
    @Test
    fun `존재하지 않는 아이디라면 레시피 조회 실패`() {
        // given

        // when

        // then
        shouldThrow<RecipeNotFoundException> { recipeService.findRecipeById(UUID.randomUUID()) }
    }

    @AfterEach
    fun deleteAll() {
        memberRepository.deleteAll()
        recipeRepository.deleteAll()
    }
}