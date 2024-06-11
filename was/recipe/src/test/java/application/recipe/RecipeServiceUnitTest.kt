package application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.example.application.memberRecipe.MemberRecipeRecommender
import org.example.application.recipe.RecipeService
import org.example.domain.entity.Member
import org.example.domain.enums.Age
import org.example.domain.enums.Gender
import org.example.domain.memberRecipe.event.AddMemberRecipeEvent
import org.example.presentation.dto.response.RecipeResponse
import org.example.domain.recipe.entity.Recipe
import org.example.domain.recipe.repository.RecipeRepository
import org.example.exception.exceptions.RecipeNotFoundException
import org.example.presentation.dto.response.RecipeDataResponse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.context.ApplicationEventPublisher
import java.util.*
import kotlin.reflect.typeOf

@ExtendWith(MockitoExtension::class)
class RecipeServiceUnitTest {

    @Mock
    private lateinit var recipeRepository: RecipeRepository

    @Mock
    private lateinit var publisher: ApplicationEventPublisher

    @Mock
    private lateinit var memberRecipeRecommender: MemberRecipeRecommender

    @InjectMocks
    private lateinit var recipeService: RecipeService

    private val member = Member.builder()
        .id(UUID.randomUUID())
        .nickname("before")
        .age(Age.TWENTIES)
        .gender(Gender.MALE)
        .email("test@test.com")
        .ingredients("0")
        .build()

    @DisplayName("레시피 단건 조회를 성공시킨다.")
    @Test
    fun `레시피 단건 조회`() {
        // given
        val uuid = UUID.randomUUID()

        // when
        Mockito.`when`(recipeRepository.findById(uuid)).thenReturn(Optional.of(Recipe()))
        Mockito.doNothing().`when`(publisher).publishEvent(Mockito.any(AddMemberRecipeEvent::class.java))

        // then
        val result =  recipeService.findRecipeById(uuid, member)
        result.recipeLink shouldBe RecipeResponse(Recipe().recipeLink).recipeLink
        Mockito.verify(publisher).publishEvent(Mockito.any(AddMemberRecipeEvent::class.java))
    }

    @DisplayName("레시피 단건 조회를 실패한다. RecipeNotFoundException.")
    @Test
    fun `레시피 단건 조회 실패 데이터가 없다`() {
        // given
        val uuid = UUID.randomUUID()

        // when
        Mockito.doThrow(RecipeNotFoundException()).`when`(recipeRepository).findById(uuid)

        // then
        shouldThrow<RecipeNotFoundException> { recipeService.findRecipeById(uuid, member) }
    }

    @DisplayName("회원의 재료와 같은 재료가 필요한 레시피를 조회한다")
    @Test
    fun `회원의 재료와 같은 재료가 필요한 레시피를 조회한다`() {
        // given

        // when
        Mockito.`when`(recipeRepository.findAll())
            .thenReturn(listOf(Recipe()))

        // then
        val result =  recipeService.findByMembersIngredients(member)
        result.size shouldBe 1
        result.get(0)::class shouldBe RecipeDataResponse::class
    }
}