package presentation

import RestDocsHelper.customDocument
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.example.FridgeApplication
import org.example.application.FridgeService
import org.example.domain.entity.Member
import org.example.domain.fridgeIngredient.repository.FridgeIngredientRepository
import org.example.domain.ingredient.entity.Ingredient
import org.example.domain.ingredient.repository.IngredientRepository
import org.example.domain.repository.MemberRepository
import org.example.infrastructure.JwtTokenProvider
import org.example.presentation.FridgeController
import org.example.presentation.dto.request.AddIngredientRequest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest(classes = [FridgeApplication::class])
@ExtendWith(RestDocumentationExtension::class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class FridgeControllerIntegrationTest(
    @Autowired private val fridgeController: FridgeController,
    @Autowired private val fridgeService: FridgeService,
    @Autowired private val memberRepository: MemberRepository,
    @Autowired private val jwtTokenProvider: JwtTokenProvider,
    @Autowired private val ingredientRepository: IngredientRepository,
    @Autowired private val fridgeIngredientRepository: FridgeIngredientRepository,
    @Autowired private val webApplicationContext: WebApplicationContext
) {

    private lateinit var member: Member
    private var accessToken: String? = null
    private lateinit var mockMvc: MockMvc
    private lateinit var ingredient: Ingredient

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
            .age(null)
            .gender(null)
            .email("test@test.com")
            .ingredients(0)
            .build()
        memberRepository.save(member)
        accessToken = jwtTokenProvider.createAccessToken(member.id.toString())

        ingredient = ingredientRepository.save(Ingredient(0, "고기"))
    }

    @DisplayName("내 냉장고에 재료를 성공적으로 추가한다.")
    @Test
    fun addIngredient_success() {
        // given
        val request = AddIngredientRequest(ingredient.id, 2018, 3, 1)

        // expected
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/fridge")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .contentType(MediaType.APPLICATION_JSON)
            .content(makeJson(request)))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andDo(customDocument(
                "add_ingredient",
                HeaderDocumentation.requestHeaders(
                    HeaderDocumentation.headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
                PayloadDocumentation.requestFields(
                    PayloadDocumentation.fieldWithPath("ingredientId").description("냉장고에 추가할 재료 id"),
                    PayloadDocumentation.fieldWithPath("year").description("유통기한의 년도"),
                    PayloadDocumentation.fieldWithPath("month").description("유통기한의 월"),
                    PayloadDocumentation.fieldWithPath("day").description("유통기한의 일"),
                ),
            )).andReturn()
    }

    @DisplayName("내 냉장고재료 추가를 실패한다. 토큰이 없을 때")
    @Test
    fun addIngredient_fail_no_token() {
        // given
        val request = AddIngredientRequest(ingredient.id, 2018, 3, 1)

        // expected
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/fridge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(makeJson(request)))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andDo(customDocument(
                createFailedIdentifier("add_ingredient", NO_TOKEN),
                PayloadDocumentation.requestFields(
                    PayloadDocumentation.fieldWithPath("ingredientId").description("냉장고에 추가할 재료 id"),
                    PayloadDocumentation.fieldWithPath("year").description("유통기한의 년도"),
                    PayloadDocumentation.fieldWithPath("month").description("유통기한의 월"),
                    PayloadDocumentation.fieldWithPath("day").description("유통기한의 일"),
                ),
            )).andReturn()
    }

    @DisplayName("내 냉장고에 재료 추가를 실패한다. 존재하지 않는 재료")
    @Test
    fun addIngredient_fail_not_found() {
        // given
        val request = AddIngredientRequest(10000, 2018, 3, 1)

        // expected
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/fridge")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(makeJson(request)))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andDo(customDocument(
                createFailedIdentifier("add_ingredient", NOT_FOUND),
                HeaderDocumentation.requestHeaders(
                    HeaderDocumentation.headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
                PayloadDocumentation.requestFields(
                    PayloadDocumentation.fieldWithPath("ingredientId").description("냉장고에 추가할 재료 id"),
                    PayloadDocumentation.fieldWithPath("year").description("유통기한의 년도"),
                    PayloadDocumentation.fieldWithPath("month").description("유통기한의 월"),
                    PayloadDocumentation.fieldWithPath("day").description("유통기한의 일"),
                ),
            )).andReturn()
    }

    @DisplayName("내 냉장고에 재료 추가를 실패한다. 재료 id/년/월/일중에 하나라도 null일 경우")
    @Test
    fun addIngredient_fail_blank() {
        // given
        val request = AddIngredientRequest(null, 2017, 3, 1)

        // expected
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/fridge")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(makeJson(request)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(customDocument(
                createFailedIdentifier("add_ingredient", BLANK),
                HeaderDocumentation.requestHeaders(
                    HeaderDocumentation.headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
                PayloadDocumentation.requestFields(
                    PayloadDocumentation.fieldWithPath("ingredientId").description("냉장고에 추가할 재료 id"),
                    PayloadDocumentation.fieldWithPath("year").description("유통기한의 년도"),
                    PayloadDocumentation.fieldWithPath("month").description("유통기한의 월"),
                    PayloadDocumentation.fieldWithPath("day").description("유통기한의 일"),
                ),
            )).andReturn()
    }

    @DisplayName("내 냉장고에 재료 추가를 실패한다. 재료가 이미 냉장고에 있는 경우")
    @Test
    fun addIngredient_fail_already_in() {
        // given
        val request = AddIngredientRequest(ingredient.id, 2018, 3, 1)
        fridgeService.addIngredient(request, member)

        // expected
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/fridge")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(makeJson(request)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(customDocument(
                createFailedIdentifier("add_ingredient", INVALID),
                HeaderDocumentation.requestHeaders(
                    HeaderDocumentation.headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
                PayloadDocumentation.requestFields(
                    PayloadDocumentation.fieldWithPath("ingredientId").description("냉장고에 추가할 재료 id"),
                    PayloadDocumentation.fieldWithPath("year").description("유통기한의 년도"),
                    PayloadDocumentation.fieldWithPath("month").description("유통기한의 월"),
                    PayloadDocumentation.fieldWithPath("day").description("유통기한의 일"),
                ),
            )).andReturn()
    }

    @DisplayName("전체 재료를 조회한다.")
    @Test
    fun findAllIngredients_success() {
        // given

        // expected
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/fridge")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(customDocument(
                "find_all_ingredients",
                HeaderDocumentation.requestHeaders(
                    HeaderDocumentation.headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
            )).andReturn()
    }

    @DisplayName("전체 재료를 조회를 실패한다. 토큰이 없을 경우")
    @Test
    fun findAllIngredients_fail_no_token() {
        // given

        // expected
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/fridge"))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andDo(customDocument(
                createFailedIdentifier("find_all_ingredients", NO_TOKEN),
            )).andReturn()
    }

    @AfterEach
    fun deleteAll() {
        memberRepository.deleteAll()
        ingredientRepository.deleteAll()
        fridgeIngredientRepository.deleteAll()
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