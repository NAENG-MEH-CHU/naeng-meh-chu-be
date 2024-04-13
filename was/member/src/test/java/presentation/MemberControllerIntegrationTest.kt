package presentation

import org.example.MemberApplication
import org.example.application.MemberService
import org.example.domain.repository.MemberRepository
import org.example.presentation.MemberController
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [MemberApplication::class])
@AutoConfigureMockMvc
class MemberControllerIntegrationTest(
        @Autowired memberController: MemberController,
        @Autowired memberService: MemberService,
        @Autowired memberRepository: MemberRepository
) {

    @DisplayName("회원의 닉네임 수정을 성공한다.")
    @Test
    fun changeNickname_success() {
        //given

        //expected

    }
}