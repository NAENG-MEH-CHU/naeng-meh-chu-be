package presentation

import RestDocsHelper.customDocument
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.example.FridgeApplication
import org.example.application.FridgeService
import org.example.domain.entity.Member
import org.example.domain.enums.Age
import org.example.domain.fridgeIngredient.entity.FridgeIngredient
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
import org.springframework.restdocs.headers.HeaderDocumentation.*
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.time.LocalDate
import java.util.*

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
    private lateinit var other: Member
    private lateinit var accessToken: String
    private lateinit var mockMvc: MockMvc
    private lateinit var ingredient: Ingredient
    private lateinit var otherToken: String

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
            .ingredients("0")
            .build()
        memberRepository.save(member)
        other = memberRepository.save(Member
            .builder()
            .nickname("other")
            .email("other@other.com")
            .ingredients("0")
            .age(Age.THIRTIES)
            .build())
        accessToken = jwtTokenProvider.createAccessToken(member.id.toString())
        otherToken = jwtTokenProvider.createAccessToken(other.id.toString())

        ingredient = ingredientRepository.save(Ingredient(0, "고기"))
    }

    @DisplayName("내 냉장고에 재료를 성공적으로 추가한다.")
    @Test
    fun addIngredient_success() {
        // given
        val request = AddIngredientRequest(ingredient.id, 2018, 3, 1)

        // expected
        mockMvc.perform(
            post("/api/fridge")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .contentType(MediaType.APPLICATION_JSON)
            .content(makeJson(request)))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andDo(customDocument(
                "add_ingredient",
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
                requestFields(
                    fieldWithPath("ingredientId").description("냉장고에 추가할 재료 id"),
                    fieldWithPath("year").description("유통기한의 년도"),
                    fieldWithPath("month").description("유통기한의 월"),
                    fieldWithPath("day").description("유통기한의 일"),
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
            post("/api/fridge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(makeJson(request)))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andDo(customDocument(
                createFailedIdentifier("add_ingredient", NO_TOKEN),
                requestFields(
                    fieldWithPath("ingredientId").description("냉장고에 추가할 재료 id"),
                    fieldWithPath("year").description("유통기한의 년도"),
                    fieldWithPath("month").description("유통기한의 월"),
                    fieldWithPath("day").description("유통기한의 일"),
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
            post("/api/fridge")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(makeJson(request)))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andDo(customDocument(
                createFailedIdentifier("add_ingredient", NOT_FOUND),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
                requestFields(
                    fieldWithPath("ingredientId").description("냉장고에 추가할 재료 id"),
                    fieldWithPath("year").description("유통기한의 년도"),
                    fieldWithPath("month").description("유통기한의 월"),
                    fieldWithPath("day").description("유통기한의 일"),
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
            post("/api/fridge")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(makeJson(request)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(customDocument(
                createFailedIdentifier("add_ingredient", INVALID),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
                requestFields(
                    fieldWithPath("ingredientId").description("냉장고에 추가할 재료 id"),
                    fieldWithPath("year").description("유통기한의 년도"),
                    fieldWithPath("month").description("유통기한의 월"),
                    fieldWithPath("day").description("유통기한의 일"),
                ),
            )).andReturn()
    }

    @DisplayName("전체 재료를 조회한다.")
    @Test
    fun findAllIngredients_success() {
        // given

        // expected
        mockMvc.perform(
            get("/api/fridge")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(customDocument(
                "find_all_ingredients",
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
                responseFields(
                    fieldWithPath("ingredients").description("재료 목록"),
                    fieldWithPath("ingredients[].ingredientId").description("재료 ID"),
                    fieldWithPath("ingredients[].name").description("재료 이름")
                )
            )).andReturn()
    }

    @DisplayName("전체 재료를 조회를 실패한다. 토큰이 없을 경우")
    @Test
    fun findAllIngredients_fail_no_token() {
        // given

        // expected
        mockMvc.perform(
            get("/api/fridge")
        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andDo(customDocument(
                createFailedIdentifier("find_all_ingredients", NO_TOKEN),
            )).andReturn()
    }

    @DisplayName("내 재료를 조회한다.")
    @Test
    fun findMyIngredients_success() {
        // given
        val request = AddIngredientRequest(ingredient.id, 2018, 3, 1)
        fridgeService.addIngredient(request, member)

        // expected
        mockMvc.perform(
            get("/api/fridge/mine")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(customDocument(
                "find_my_ingredients",
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
                responseFields(
                    fieldWithPath("myIngredients").description("내 재료 목록"),
                    fieldWithPath("myIngredients[].id").description("내 재료 ID"),
                    fieldWithPath("myIngredients[].name").description("재료 이름"),
                    fieldWithPath("myIngredients[].ingredientId").description("재료 ID"),
                    fieldWithPath("myIngredients[].dueDay").description("재료 유통기한")
                )
            )).andReturn()
    }

    @DisplayName("내 재료를 조회를 실패한다. 토큰이 없을 경우")
    @Test
    fun findMyIngredients_fail_no_token() {
        // given
        val request = AddIngredientRequest(ingredient.id, 2018, 3, 1)
        fridgeService.addIngredient(request, member)

        // expected
        mockMvc.perform(
            get("/api/fridge/mine")
        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andDo(customDocument(
                createFailedIdentifier("find_my_ingredients", NO_TOKEN),
            )).andReturn()
    }

    @DisplayName("유통기한 임박 재료를 조회한다. 등록 기간이 범위 내면 데이터가 반환된다.")
    @Test
    fun findUpcomingIngredients_success_in_due_days() {
        // given
        val deadLine = LocalDate.now().plusDays(2L)
        val request = AddIngredientRequest(ingredient.id, deadLine.year, deadLine.monthValue, deadLine.dayOfMonth)
        fridgeService.addIngredient(request, member)

        // expected
        mockMvc.perform(
            get("/api/fridge/upcoming/3")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.myIngredients").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("$.myIngredients.length()").value(1))
            .andDo(customDocument(
                "find_upcoming_ingredients",
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
                responseFields(
                    fieldWithPath("myIngredients").description("내 재료 목록"),
                    fieldWithPath("myIngredients[].id").description("내 재료 ID"),
                    fieldWithPath("myIngredients[].name").description("재료 이름"),
                    fieldWithPath("myIngredients[].ingredientId").description("재료 ID"),
                    fieldWithPath("myIngredients[].dueDay").description("재료 유통기한")
                )
            )).andReturn()
    }

    @DisplayName("유통기한 임박 재료를 조회한다. 등록 기간이 범위 밖이면 데이터가 반횐되지 않는다.")
    @Test
    fun findUpcomingIngredients_success_out_due_days() {
        // given
        val deadLine = LocalDate.now().plusDays(2L)
        val request = AddIngredientRequest(ingredient.id, deadLine.year, deadLine.monthValue, deadLine.dayOfMonth)
        fridgeService.addIngredient(request, member)

        // expected
        mockMvc.perform(
            get("/api/fridge/upcoming/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.myIngredients").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("$.myIngredients.length()").value(0))
            .andDo(customDocument(
                "find_upcoming_ingredients_due_date_out",
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
            )).andReturn()
    }

    @DisplayName("유통기한 임박 재료를 조회를 실패한다. 토큰이 없을 경우")
    @Test
    fun findUpcomingIngredients_fail_no_token() {
        // given
        val request = AddIngredientRequest(ingredient.id, 2018, 3, 1)
        fridgeService.addIngredient(request, member)

        // expected
        mockMvc.perform(
            get("/api/fridge/upcoming/1")
        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andDo(customDocument(
                createFailedIdentifier("find_upcoming_ingredients", NO_TOKEN),
            )).andReturn()
    }

    @DisplayName("내 재료를 삭제한다.")
    @Test
    fun deleteMyIngredient_success() {
        // given
        val fridgeIngredient = fridgeIngredientRepository
            .save(FridgeIngredient(member.id, ingredient.id, "계란", LocalDate.now()))

        // expected
        mockMvc.perform(
            delete("/api/fridge/${fridgeIngredient.id}")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken"))
            .andExpect(MockMvcResultMatchers.status().isNoContent)
            .andDo(customDocument(
                "delete_my_ingredient",
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
            )).andReturn()
    }

    @DisplayName("내 재료를 삭제를 실패한다. 토큰이 없을 경우")
    @Test
    fun deleteMyIngredient_fail_no_token() {
        // given
        val fridgeIngredient = fridgeIngredientRepository
            .save(FridgeIngredient(member.id, ingredient.id, "계란", LocalDate.now()))

        // expected
        mockMvc.perform(
            delete("/api/fridge/${fridgeIngredient.id}")
        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andDo(customDocument(
                createFailedIdentifier("delete_my_ingredient", NO_TOKEN),
            )).andReturn()
    }

    @DisplayName("내 재료를 삭제를 실패한다. 재료가 없는 경우")
    @Test
    fun deleteMyIngredient_fail_not_found() {
        // given

        // expected
        mockMvc.perform(
            delete("/api/fridge/${UUID.randomUUID()}")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andDo(customDocument(
                createFailedIdentifier("delete_my_ingredient", NOT_FOUND),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
            )).andReturn()
    }

    @DisplayName("내 재료를 삭제를 실패한다. 내 재료가 아닌 경우")
    @Test
    fun deleteMyIngredient_fail_forbidden() {
        // given
        val fridgeIngredient = fridgeIngredientRepository
            .save(FridgeIngredient(member.id, ingredient.id, "계란", LocalDate.now()))

        // expected
        mockMvc.perform(
            delete("/api/fridge/${fridgeIngredient.id}")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $otherToken")
        )
            .andExpect(MockMvcResultMatchers.status().isForbidden)
            .andDo(customDocument(
                createFailedIdentifier("delete_my_ingredient", FORBIDDEN),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
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