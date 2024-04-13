package presentation

import org.example.MemberApplication
import org.example.application.MemberService
import org.example.domain.entity.Member
import org.example.domain.enums.Gender
import org.example.domain.repository.MemberRepository
import org.example.infrastructure.JwtTokenProvider
import org.example.presentation.MemberController
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [MemberApplication::class])
@AutoConfigureMockMvc
class MemberControllerIntegrationTest(
        @Autowired private val memberController: MemberController,
        @Autowired private val memberService: MemberService,
        @Autowired private val memberRepository: MemberRepository,
        @Autowired private val jwtTokenProvider: JwtTokenProvider
) {

    private lateinit var member: Member
    private var accessToken: String? = null

    @BeforeEach
    fun addMember() {
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

    @DisplayName("회원의 닉네임 수정을 성공한다.")
    @Test
    fun changeNickname_success() {
        //given
        val nickname = "after"

        //expected

    }

    @AfterEach
    fun removeMember() {
        memberRepository.delete(member)
        accessToken = null
    }
}