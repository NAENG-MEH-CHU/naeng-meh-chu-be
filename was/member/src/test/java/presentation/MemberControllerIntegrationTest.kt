package presentation

import RestDocsHelper.customDocument
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import org.example.MemberApplication
import org.example.application.MemberService
import org.example.domain.entity.Member
import org.example.domain.entity.MemberReason
import org.example.domain.enums.Age
import org.example.domain.enums.Gender
import org.example.domain.enums.UsingReason
import org.example.domain.repository.MemberReasonRepository
import org.example.domain.repository.MemberRepository
import org.example.infrastructure.JwtTokenProvider
import org.example.presentation.MemberController
import org.example.presentation.dto.request.AddUsingReasonRequest
import org.example.presentation.dto.request.ChangeAgeRequest
import org.example.presentation.dto.request.ChangeGenderRequest
import org.example.presentation.dto.request.ChangeNicknameRequest
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
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.util.*


@SpringBootTest(classes = [MemberApplication::class])
@ExtendWith(RestDocumentationExtension::class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class MemberControllerIntegrationTest(
        @Autowired private val memberController: MemberController,
        @Autowired private val memberService: MemberService,
        @Autowired private val memberRepository: MemberRepository,
        @Autowired private val jwtTokenProvider: JwtTokenProvider,
        @Autowired private val memberReasonRepository: MemberReasonRepository,
        @Autowired private val webApplicationContext: WebApplicationContext
) {

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
                .apply<DefaultMockMvcBuilder>(documentationConfiguration(restDocumentation))
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
    }

    @DisplayName("등록 가능한 앱의 이용사유를 조회한다")
    @Test
    fun findAllUsingReasons_success() {
        mockMvc.perform(
            get("/api/member/reasons")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(customDocument(
                "find_all_using_reasons",
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
            )).andReturn()
    }

    @DisplayName("등록 가능한 앱의 이용사유를 조회를 실패한다. 토큰이 없을 경우")
    @Test
    fun findAllUsingReasons_fail_no_token() {
        mockMvc.perform(
            get("/api/member/reasons")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(customDocument(
                createFailedIdentifier("find_all_using_reasons", NO_TOKEN),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
            )).andReturn()
    }

    @DisplayName("나의 앱의 이용사유를 조회한다")
    @Test
    fun findMyUsingReasons_success() {
        mockMvc.perform(
            get("/api/member/reasons/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(customDocument(
                "find_my_using_reasons",
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
            )).andReturn()
    }

    @DisplayName("나의 앱의 이용사유를 조회를 실패한다. 토큰이 없을 경우")
    @Test
    fun findMyUsingReasons_fail_no_token() {
        mockMvc.perform(
            get("/api/member/reasons/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(customDocument(
                createFailedIdentifier("find_my_using_reasons", NO_TOKEN),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
            )).andReturn()
    }

    @DisplayName("나의 앱 이용사유를 추가한다")
    @Test
    fun addUsingReasons_success() {
        // given
        val request = AddUsingReasonRequest(listOf(UsingReason.HEALTH.content))

        // expected
        mockMvc.perform(
            post("/api/member/reasons")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(makeJson(request)))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andDo(customDocument(
                "add_using_reason",
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
                requestFields(
                    fieldWithPath("reasons").description("저장할 앱 이용사유들. 배열형으로")
                )
            )).andReturn()

        memberService.findMyUsingReasons(member).size shouldBe 1
    }

    @DisplayName("나의 앱 이용사유 추가를 실패한다. 토큰이 없을 경우")
    @Test
    fun addUsingReasons_fail_no_token() {
        // given
        val request = AddUsingReasonRequest(listOf(UsingReason.HEALTH.content))

        // expected
        mockMvc.perform(
            post("/api/member/reasons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(makeJson(request)))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andDo(customDocument(
                createFailedIdentifier("add_using_reason", NO_TOKEN),
                requestFields(
                    fieldWithPath("reasons").description("저장할 앱 이용사유들. 배열형으로")
                )
            )).andReturn()
    }

    @DisplayName("나의 앱 이용사유 추가를 실패한다. 입력이 없는 경우")
    @Test
    fun addUsingReasons_fail_blank() {
        // given

        // expected
        mockMvc.perform(
            post("/api/member/reasons")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(customDocument(
                createFailedIdentifier("add_using_reason", BLANK),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
            )).andReturn()
    }

    @DisplayName("나의 앱 이용사유 추가를 실패한다. 입력이 이상할 경우")
    @Test
    fun addUsingReasons_fail_invalid() {
        // given
        val request = AddUsingReasonRequest(listOf("없는거"))

        // expected
        mockMvc.perform(
            post("/api/member/reasons")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(makeJson(request)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(customDocument(
                createFailedIdentifier("add_using_reason", INVALID),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
                requestFields(
                    fieldWithPath("reasons").description("저장할 앱 이용사유들. 배열형으로")
                )
            )).andReturn()
    }

    @DisplayName("회원의 정보 조회를 성공한다")
    @Test
    fun findMemberData_success() {
        mockMvc.perform(
            get("/api/member/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(customDocument(
                "find_member_data",
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
            )).andReturn()
    }

    @DisplayName("회원의 정보 조회를 실패한다. 토큰이 없을 경우")
    @Test
    fun findMemberData_fail_no_token() {

        mockMvc.perform(get("/api/member/me"))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andDo(customDocument(
                createFailedIdentifier("find_member_data", NO_TOKEN),
            )).andReturn();
    }

    @DisplayName("변경 가능한 나이대 조회를 성공한다")
    @Test
    fun findAgeOption_success() {
        mockMvc.perform(
            get("/api/member/age")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(customDocument(
                "find_age_option",
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
            )).andReturn()
    }

    @DisplayName("변경 가능한 나이대 조회를 실패한다. 토큰이 없을 경우")
    @Test
    fun findAgeOption_fail_no_token() {

        mockMvc.perform(get("/api/member/age"))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andDo(customDocument(
                createFailedIdentifier("find_age", NO_TOKEN),
            )).andReturn();
    }

    @DisplayName("회원의 앱 이용사유 삭제를 성공한다.")
    @Test
    fun deleteUsingReason_success() {
        // given
        val memberReason = memberReasonRepository.save(MemberReason(member.id, UsingReason.HEALTH)).id

        // expected
        mockMvc.perform(
            delete("/api/member/reasons?id=${memberReason}")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken"))
            .andExpect(MockMvcResultMatchers.status().isNoContent)
            .andDo(customDocument(
                "delete_using_reason",
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
            )).andReturn()
    }

    @DisplayName("회원의 앱 이용사유 삭제를 실패한다. 토큰이 없을 경우")
    @Test
    fun deleteUsingReason_fail_no_token() {
        // given
        val memberReason = memberReasonRepository.save(MemberReason(member.id, UsingReason.HEALTH)).id

        // expected
        mockMvc.perform(
            delete("/api/member/reasons?id=${memberReason}"))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andDo(customDocument(
                createFailedIdentifier("delete_using_reason", NO_TOKEN),
            )).andReturn()
    }

    @DisplayName("회원의 앱 이용사유 삭제를 실패한다. 파라미터가 없는 경우")
    @Test
    fun deleteUsingReason_fail_blank() {
        // given
        val memberReason = memberReasonRepository.save(MemberReason(member.id, UsingReason.HEALTH)).id

        // expected
        mockMvc.perform(
            delete("/api/member/reasons")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(customDocument(
                createFailedIdentifier("delete_using_reason", BLANK),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
            )).andReturn()
    }

    @DisplayName("회원의 앱 이용사유 삭제를 실패한다. 데이터가 없는 경우")
    @Test
    fun deleteUsingReason_fail_INVALID() {
        // given
        val memberReason = memberReasonRepository.save(MemberReason(member.id, UsingReason.HEALTH)).id

        // expected
        mockMvc.perform(
            delete("/api/member/reasons?id=${UUID.randomUUID()}")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken"))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andDo(customDocument(
                createFailedIdentifier("delete_using_reason", NOT_FOUND),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
            )).andReturn()
    }

    @DisplayName("회원의 앱 이용사유 삭제를 실패한다. 다른 사람의 데이터를 삭제하려 한 경우")
    @Test
    fun deleteUsingReason_fail_forbidden() {
        // given
        val other = Member.builder().nickname("other").age(Age.TWENTIES).gender(Gender.MALE).email("other@other.com").build();

        val memberReason = memberReasonRepository.save(MemberReason(memberRepository.save(other).id, UsingReason.HEALTH)).id

        // expected
        mockMvc.perform(
            delete("/api/member/reasons?id=${memberReason}")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken"))
            .andExpect(MockMvcResultMatchers.status().isForbidden)
            .andDo(customDocument(
                createFailedIdentifier("delete_using_reason", FORBIDDEN),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
            )).andReturn()
    }

    @DisplayName("회원의 닉네임 수정을 성공한다")
    @Test
    fun updateNickname_success() {
        val newNickname = "after"

        mockMvc.perform(patch("/api/member/nickname")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(makeJson(ChangeNicknameRequest(newNickname))))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andDo(customDocument(
                        "update_nickname",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        requestFields(
                                fieldWithPath("nickname").description("변경할 닉네임"),
                        ),
                )).andReturn()
    }

    @DisplayName("회원의 닉네임 수정을 실패한다. 토큰이 없을 경우")
    @Test
    fun updateNickname_fail_no_token() {
        val newNickname = "after"

        mockMvc.perform(patch("/api/member/nickname")
                .contentType(MediaType.APPLICATION_JSON)
                .content(makeJson(ChangeNicknameRequest(newNickname))))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
                .andDo(customDocument(
                        createFailedIdentifier("update_nickname", NO_TOKEN),
                        requestFields(
                                fieldWithPath("nickname").description("변경할 닉네임"),
                        ),
                )).andReturn();
    }

    @DisplayName("회원의 닉네임 수정을 실패한다. 입력이 없을 경우")
    @Test
    fun updateNickname_fail_no_nickname() {

        mockMvc.perform(patch("/api/member/nickname")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andDo(customDocument(
                        createFailedIdentifier("update_nickname", BLANK),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                )).andReturn();
    }

    @DisplayName("회원의 성별 수정을 성공한다")
    @Test
    fun updateGender_success() {
        val newGender = "여성"

        mockMvc.perform(patch("/api/member/gender")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .contentType(MediaType.APPLICATION_JSON)
            .content(makeJson(ChangeGenderRequest(newGender))))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(customDocument(
                "update_gender",
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
                requestFields(
                    fieldWithPath("gender").description("변경할 성별(`남성`, `여성` 으로 입력)"),
                ),
            )).andReturn()
    }

    @DisplayName("회원의 성별 수정을 실패한다. 토큰이 없을 경우")
    @Test
    fun updateGender_fail_no_token() {
        val newGender = "여성"

        mockMvc.perform(patch("/api/member/gender")
            .contentType(MediaType.APPLICATION_JSON)
            .content(makeJson(ChangeGenderRequest(newGender))))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andDo(customDocument(
                createFailedIdentifier("update_gender", NO_TOKEN),
                requestFields(
                    fieldWithPath("gender").description("변경할 성별(`남성`, `여성` 으로 입력)"),
                ),
            )).andReturn();
    }

    @DisplayName("회원의 성별 수정을 실패한다. 입력이 없을 경우")
    @Test
    fun updateGender_fail_no_gender() {

        mockMvc.perform(patch("/api/member/gender")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(customDocument(
                createFailedIdentifier("update_gender", BLANK),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
            )).andReturn();
    }

    @DisplayName("회원의 성별 수정을 실패한다. 입력이 `남성/여성`이 아닌경우 경우")
    @Test
    fun updateGender_fail_invalid_gender() {

        mockMvc.perform(patch("/api/member/gender")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .contentType(MediaType.APPLICATION_JSON)
            .content(makeJson(ChangeGenderRequest("중성"))))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(customDocument(
                createFailedIdentifier("update_gender", INVALID),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
                requestFields(
                    fieldWithPath("gender").description("변경할 성별(`남성`, `여성` 으로 입력)"),
                ),
            )).andReturn();
    }

    @DisplayName("회원의 나이대 수정을 성공한다")
    @Test
    fun updateAge_success() {
        val request = ChangeAgeRequest("30대")

        mockMvc.perform(patch("/api/member/age")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .contentType(MediaType.APPLICATION_JSON)
            .content(makeJson(request)))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(customDocument(
                "update_age",
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
                requestFields(
                    fieldWithPath("age").description("변경할 나이대"),
                ),
            )).andReturn()
    }

    @DisplayName("회원의 성별 수정을 실패한다. 토큰이 없을 경우")
    @Test
    fun updateAge_fail_no_token() {
        val request = ChangeAgeRequest("30대")

        mockMvc.perform(patch("/api/member/age")
            .contentType(MediaType.APPLICATION_JSON)
            .content(makeJson(request)))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andDo(customDocument(
                createFailedIdentifier("update_age", NO_TOKEN),
                requestFields(
                    fieldWithPath("age").description("변경할 나이대"),
                ),
            )).andReturn();
    }

    @DisplayName("회원의 나이대 입력이 없으면 수정을 실패한다")
    @Test
    fun updateAge_fail_no_age() {

        mockMvc.perform(patch("/api/member/age")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(customDocument(
                createFailedIdentifier("update_age", BLANK),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
            )).andReturn();
    }

    @DisplayName("회원의 나이대 입력이 올바르지 않으면 수정을 실패한다.")
    @Test
    fun updateAge_fail_invalid_age() {
        val request = ChangeAgeRequest("123456")

        mockMvc.perform(patch("/api/member/age")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .contentType(MediaType.APPLICATION_JSON)
            .content(makeJson(request)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(customDocument(
                createFailedIdentifier("update_age", INVALID),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
                requestFields(
                    fieldWithPath("age").description("잘못된 나이대 입력"),
                ),
            )).andReturn();
    }

    @DisplayName("회원 삭제를 성공한다")
    @Test
    fun deleteMember_success() {
        mockMvc.perform(delete("/api/member")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken"))
            .andExpect(MockMvcResultMatchers.status().isNoContent)
            .andDo(customDocument(
                "delete_member",
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
            )).andReturn()
    }

    @DisplayName("회원의 삭제를 실패한다. 토큰이 없을 경우")
    @Test
    fun deleteMember_fail_no_token() {

        mockMvc.perform(delete("/api/member"))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andDo(customDocument(
                createFailedIdentifier("delete_member", NO_TOKEN)
            )).andReturn();
    }

    @AfterEach
    fun removeMember() {
        memberRepository.delete(member)
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