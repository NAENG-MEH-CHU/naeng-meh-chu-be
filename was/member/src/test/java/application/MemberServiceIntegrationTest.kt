package application

import org.example.MemberApplication
import org.example.application.MemberService
import org.example.domain.entity.Member
import org.example.domain.enums.Gender
import org.example.domain.repository.MemberRepository
import org.example.exception.exceptions.MemberNotFoundException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [MemberApplication::class])
@AutoConfigureMockMvc
open class MemberServiceIntegrationTest(
        @Autowired private var memberService: MemberService,
        @Autowired private var memberRepository: MemberRepository){

    private lateinit var member: Member

    @BeforeEach
    fun addMember() {
        member = Member.builder()
                .nickname("before")
                .age(10)
                .gender(Gender.MALE)
                .email("test@test.com")
                .ingredients("0")
                .build();
        member = memberRepository.save(member);
    }

    @DisplayName("회원의 닉네임 변경을 성공한다.")
    @Test
    fun changeNickname_success() {
        // given

        //when
        memberService.updateNickname("after", member)
        val changedMember = memberRepository.findById(member.id).orElseThrow{ MemberNotFoundException() }

        //then
        Assertions.assertEquals(changedMember.nickname, "after");
    }
}