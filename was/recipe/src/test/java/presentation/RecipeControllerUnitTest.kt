package presentation

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import org.example.application.recipe.RecipeService
import org.example.domain.entity.Member
import org.example.domain.enums.Age
import org.example.domain.enums.Gender
import org.example.domain.recipe.dto.RecipeResponse
import org.example.exception.exceptions.RecipeNotFoundException
import org.example.presentation.RecipeController
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class RecipeControllerUnitTest {

    @Mock
    private lateinit var recipeService: RecipeService

    @InjectMocks
    private lateinit var recipeController: RecipeController

    private val member = Member.builder()
    .id(UUID.randomUUID())
    .nickname("before")
    .age(Age.TWENTIES)
    .gender(Gender.MALE)
    .email("test@test.com")
    .ingredients(0)
    .build()

    @DisplayName("레시피 단건 조회를 성공한다")
    @Test
    fun `레시피 단건 조회를 성공한다`() {
        // given
        val id = UUID.randomUUID()
        val response = RecipeResponse("testLint")

        // when
        Mockito.`when`(recipeService.findRecipeById(id, member))
            .thenReturn(response)

        // then
        recipeController.findRecipeById(member, id) shouldBeEqual ResponseEntity<RecipeResponse>(response, HttpStatus.OK)
    }

    @DisplayName("없는 id로 조회하면 실패한다")
    @Test
    fun `없는 id로 조회하면 실패한다`() {
        // given
        val id = UUID.randomUUID()

        // when
        Mockito.doThrow(RecipeNotFoundException()).`when`(recipeService).findRecipeById(id, member)

        // then
        shouldThrow<RecipeNotFoundException> { recipeController.findRecipeById(member, id) }
    }
}