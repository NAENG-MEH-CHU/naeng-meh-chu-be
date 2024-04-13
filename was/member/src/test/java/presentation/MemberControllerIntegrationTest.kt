package presentation

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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext


@SpringBootTest(classes = [MemberApplication::class])
@AutoConfigureMockMvc
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
    fun formerSetup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();

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

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/member/nickname")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(makeJson(ChangeNicknameRequest(newNickname))))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @DisplayName("회원의 닉네임 수정을 실패한다. 토큰이 없을 경우")
    @Test
    fun updateNickname_fail_no_token() {
        val newNickname = "after"

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/member/nickname")
                .contentType(MediaType.APPLICATION_JSON)
                .content(makeJson(ChangeNicknameRequest(newNickname))))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @DisplayName("회원의 닉네임 수정을 실패한다. 입력이 없을 경우")
    @Test
    fun updateNickname_fail_no_nickname() {

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/member/nickname")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
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