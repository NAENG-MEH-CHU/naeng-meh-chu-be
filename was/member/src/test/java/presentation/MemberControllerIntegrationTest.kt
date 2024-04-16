package presentation

import RestDocsHelper.customDocument
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.example.MemberApplication
import org.example.application.MemberService
import org.example.domain.entity.Member
import org.example.domain.repository.MemberRepository
import org.example.infrastructure.JwtTokenProvider
import org.example.presentation.MemberController
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


@SpringBootTest(classes = [MemberApplication::class])
@ExtendWith(RestDocumentationExtension::class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class MemberControllerIntegrationTest(
        @Autowired private val memberController: MemberController,
        @Autowired private val memberService: MemberService,
        @Autowired private val memberRepository: MemberRepository,
        @Autowired private val jwtTokenProvider: JwtTokenProvider,
        @Autowired private val webApplicationContext: WebApplicationContext
) {

    private lateinit var member: Member
    private var accessToken: String? = null
    private lateinit var mockMvc: MockMvc

    private val FAIL_PREFIX = "fail_to_"
    private val NO_TOKEN = "_no_token"
    private val INVALID = "_invalid"
    private val BLANK = "_blank"

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
                createIdentifier("find_all_using_reasons", false, NO_TOKEN),
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
                createIdentifier("find_my_using_reasons", false, NO_TOKEN),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
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
                createIdentifier("find_member_data", false, NO_TOKEN),
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
                createIdentifier("find_age", false, NO_TOKEN),
            )).andReturn();
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
                        createIdentifier("update_nickname", false, NO_TOKEN),
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
                        createIdentifier("update_nickname", false, INVALID),
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
                createIdentifier("update_gender", false, NO_TOKEN),
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
                createIdentifier("update_gender", false, BLANK),
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
                createIdentifier("update_gender", false, INVALID),
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
                createIdentifier("update_age", false, NO_TOKEN),
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
                createIdentifier("update_age", false, BLANK),
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
                createIdentifier("update_age", false, INVALID),
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
                createIdentifier("delete_member", false, NO_TOKEN)
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

    private fun createIdentifier(name: String, succeeded: Boolean, reason: String?): String {
        if(succeeded) return name;
        return FAIL_PREFIX + name + reason;
    }
}