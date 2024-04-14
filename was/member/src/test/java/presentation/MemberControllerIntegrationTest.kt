package presentation

import RestDocsHelper.customDocument
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.example.MemberApplication
import org.example.application.MemberService
import org.example.domain.entity.Member
import org.example.domain.enums.Gender
import org.example.domain.repository.MemberRepository
import org.example.infrastructure.JwtTokenProvider
import org.example.presentation.MemberController
import org.example.presentation.dto.ChangeNicknameRequest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
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

    @BeforeEach
    fun formerSetup(restDocumentation: RestDocumentationContextProvider) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply<DefaultMockMvcBuilder>(documentationConfiguration(restDocumentation))
                .build()

        member = Member.builder()
                .nickname("before")
                .age(null)
                .gender(Gender.MALE)
                .email("test@test.com")
                .ingredients(0)
                .build()
        memberRepository.save(member)
        accessToken = jwtTokenProvider.createAccessToken(member.id.toString())
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
                        "fail_to_update_nickname_no_token",
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
                        "fail_to_update_nickname_no_nickname",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
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
}