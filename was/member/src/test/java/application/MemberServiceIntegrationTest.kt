package application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.example.MemberApplication
import org.example.application.MemberService
import org.example.domain.entity.Member
import org.example.domain.enums.Gender
import org.example.domain.repository.MemberRepository
import org.example.exception.exceptions.GenderNotValidException
import org.example.exception.exceptions.MemberNotFoundException
import org.example.presentation.dto.ChangeAgeRequest
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
        changedMember.nickname shouldBe "after"
    }

    @DisplayName("회원의 성별 변경을 성공한다.")
    @Test
    fun changeGender_success() {
        // given

        //when
        memberService.updateGender("여성", member)
        val changedMember = memberRepository.findById(member.id).orElseThrow{ MemberNotFoundException() }

        //then
        changedMember.gender shouldBe  Gender.FEMALE
    }

    @DisplayName("회원의 성별 변경을 실패한다. 올바른 성별 입력이 아니다")
    @Test
    fun changeGender_fail_not_valid_gender() {
        // given

        //when

        //then
        shouldThrow<GenderNotValidException> {
            memberService.updateGender("중성", member)
        }
    }

    @DisplayName("회원의 나이대 변경을 성공한다.")
    @Test
    fun changeAge_success() {
        // given

        //when
        memberService.updateAge(ChangeAgeRequest("30대"), member)
        val changedMember = memberRepository.findById(member.id).orElseThrow{ MemberNotFoundException() }

        //then
        changedMember.gender shouldBe  Gender.FEMALE
    }

    @DisplayName("회원의 나이대 변경을 실패한다. 올바른 나이대 입력이 아니다")
    @Test
    fun changeAge_fail_not_valid_age() {
        // given

        //when

        //then
        shouldThrow<AgeNotValidException> {
            memberService.updateAge(ChangeAgeRequest("111"), member)
        }
    }

    @DisplayName("회원의 삭제를 성공한다.")
    @Test
    fun deleteMember_success() {
        // given

        //when
        memberService.deleteMember(member)

        //then
        shouldThrow<MemberNotFoundException> {
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
        shouldThrow<MemberNotFoundException> {
            memberService.deleteMember(member)
        }
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