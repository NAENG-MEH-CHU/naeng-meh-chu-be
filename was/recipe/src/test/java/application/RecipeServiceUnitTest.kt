package application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.example.application.recipe.RecipeService
import org.example.domain.entity.Member
import org.example.domain.enums.Age
import org.example.domain.enums.Gender
import org.example.domain.memberRecipe.event.AddMemberRecipeEvent
import org.example.domain.recipe.dto.RecipeResponse
import org.example.domain.recipe.entity.Recipe
import org.example.domain.recipe.repository.RecipeRepository
import org.example.exception.exceptions.RecipeNotFoundException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.context.ApplicationEventPublisher
import java.util.*

@ExtendWith(MockitoExtension::class)
class RecipeServiceUnitTest {

    @Mock
    private lateinit var recipeRepository: RecipeRepository

    @Mock
    private lateinit var publisher: ApplicationEventPublisher

    @InjectMocks
    private lateinit var recipeService: RecipeService

    private val member = Member.builder()
        .id(UUID.randomUUID())
        .nickname("before")
        .age(Age.TWENTIES)
        .gender(Gender.MALE)
        .email("test@test.com")
        .ingredients(0)
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
}