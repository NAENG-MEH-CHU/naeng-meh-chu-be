package presentation

import RestDocsHelper.customDocument
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.example.RecipeApplication
import org.example.application.memberRecipe.MemberRecipeEventHandler
import org.example.application.recipe.RecipeService
import org.example.domain.entity.Member
import org.example.domain.enums.Age
import org.example.domain.enums.Gender
import org.example.domain.memberRecipe.repository.MemberRecipeRepository
import org.example.domain.recipe.entity.Recipe
import org.example.domain.recipe.repository.RecipeRepository
import org.example.domain.repository.MemberRepository
import org.example.infrastructure.JwtTokenProvider
import org.example.presentation.RecipeController
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.HttpHeaders
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.headers.HeaderDocumentation.*
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.util.*

@SpringBootTest(classes = [RecipeApplication::class])
@ExtendWith(RestDocumentationExtension::class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class RecipeControllerIntegrationTest(
    @Autowired private val recipeController: RecipeController,
    @Autowired private val recipeService: RecipeService,
    @Autowired private val memberRecipeEventHandler: MemberRecipeEventHandler,
    @Autowired private val memberRepository: MemberRepository,
    @Autowired private val jwtTokenProvider: JwtTokenProvider,
    @Autowired private val recipeRepository: RecipeRepository,
    @Autowired private val memberRecipeRepository: MemberRecipeRepository,
    @Autowired private val webApplicationContext: WebApplicationContext,
    @Autowired private val publisher: ApplicationEventPublisher
)  {

    private lateinit var member: Member
    private var accessToken: String? = null
    private lateinit var mockMvc: MockMvc

    private val FAIL_PREFIX = "fail_to_"
    private val NO_TOKEN = "_no_token"
    private val INVALID = "_invalid"
    private val BLANK = "_blank"
    private val FORBIDDEN = "_forbidden"
    private val NOT_FOUND = "_not_found"

    @BeforeEach
    fun formerSetup(restDocumentation: RestDocumentationContextProvider) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply<DefaultMockMvcBuilder>(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
            .build()

        member = Member.builder()
            .nickname("before")
            .age(Age.TWENTIES)
            .gender(Gender.MALE)
            .email("test@test.com")
            .ingredients(0)
            .build()
        memberRepository.save(member)
        accessToken = jwtTokenProvider.createAccessToken(member.id.toString())
    }

    @DisplayName("레시피 단건을 조회한다")
    @Test
    fun `레시피 단건을 조회한다`() {
        // given
        val recipe = recipeRepository.save(Recipe(0, "test", "link", "thumbnail"))
        // expected
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/recipe/${recipe.id}")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(customDocument(
                "find_single_recipe",
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
            ))
            .andReturn()
    }

    @DisplayName("토큰이 없으면 레시피 단건 조회를 실패한다")
    @Test
    fun `토큰이 없으면 레시피 단건 조회를 실패한다`() {
        // given
        val recipe = recipeRepository.save(Recipe(0, "test", "link", "thumbnail"))

        // expected
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/recipe/${recipe.id}"))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andDo(customDocument(
                createFailedIdentifier("find_single_recipe", NO_TOKEN),
            ))
            .andReturn()
    }

    @DisplayName("존재하지 않는 레시피를 조회하면 실패한다")
    @Test
    fun `존재하지 않는 레시피를 조회하면 실패한다`() {
        // given
        val recipe = recipeRepository.save(Recipe(0, "test", "link", "thumbnail"))

        // expected
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/recipe/${UUID.randomUUID()}")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken"))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andDo(customDocument(
                createFailedIdentifier("find_single_recipe", NOT_FOUND),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
            ))
            .andReturn()
    }

    @AfterEach
    fun removeMember() {
        memberRepository.deleteAll()
        recipeRepository.deleteAll()
        memberRecipeRepository.deleteAll()
        accessToken = null
    }

    private fun makeJson(`object`: Any): String {
        try {
            return ObjectMapper().writeValueAsString(`object`)
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }
    }

    private fun createFailedIdentifier(name: String, reason: String?): String {
        return FAIL_PREFIX + name + reason;
    }
}