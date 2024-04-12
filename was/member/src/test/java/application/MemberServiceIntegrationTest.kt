package application

import org.example.MemberApplication
import org.example.application.MemberService
import org.example.domain.entity.Member
import org.example.domain.enums.Gender
import org.example.domain.repository.MemberRepository
import org.example.exception.exceptions.MemberNotFoundException
import org.junit.jupiter.api.AfterEach
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
                .age(null)
                .gender(Gender.MALE)
                .email("test@test.com")
                .ingredients(0)
                .build()
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

    @DisplayName("회원의 성별 변경을 성공한다.")
    @Test
    fun changeGender_success() {
        // given

        //when
        memberService.updateGender("여성", member)
        val changedMember = memberRepository.findById(member.id).orElseThrow{ MemberNotFoundException() }

        //then
        Assertions.assertEquals(changedMember.gender, Gender.FEMALE);
    }

    @DisplayName("회원의 삭제를 성공한다.")
    @Test
    fun deleteMember_success() {
        // given

        //when
        memberService.deleteMember(member)
        val changedMember = memberRepository.findById(member.id)

        //then
        Assertions.assertThrows(MemberNotFoundException::class.java){
            memberRepository.findById(member.id).orElseThrow{ MemberNotFoundException() }
        }
    }

    @DisplayName("회원의 삭제를 실패한다. 회원이 이미 존재하지 않는다.")
    @Test
    fun deleteMember_fail_not_found() {
        // given

        //when
        memberRepository.delete(member)

        //then
        Assertions.assertThrows(MemberNotFoundException::class.java){ memberService.deleteMember(member) }
    }

    @AfterEach
    fun deleteMember() {
        try {
            memberRepository.delete(member)
        }catch (e: Exception) {
            return
        }
    }
}
