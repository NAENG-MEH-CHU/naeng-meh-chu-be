package application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
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
import org.example.exception.exceptions.*
import org.example.presentation.dto.request.ChangeAgeRequest
import org.junit.jupiter.api.AfterEach
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
        @Autowired private var memberRepository: MemberRepository,
        @Autowired private var memberReasonRepository: MemberReasonRepository){

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

    @DisplayName("이용 사유 저장을 성공한다")
    @Test
    fun addUsingReasons_success() {
        // given
        val requests = UsingReason.entries.map { reason -> reason.content }

        // when
        memberService.addUsingReasons(requests, member)

        // then
        memberReasonRepository.findAllByMemberId(member.id).map{ each -> each.reason.content } shouldContainExactlyInAnyOrder requests
    }

    @DisplayName("이용 사유 저장을 실패한다. 존재하지 않는 이용사유이다")
    @Test
    fun addUsingReasons_fail_not_found() {
        // given
        val requests: MutableList<String> = ArrayList<String>()
        requests.add("new request")

        // when

        // then
        shouldThrow<UsingReasonUnableException> { memberService.addUsingReasons(requests, member) }
    }

    @DisplayName("전체 이용 사유 조회를 성공한다")
    @Test
    fun findAllUsingReasons_success() {
        // given

        // when

        // then
        memberService.findAllUsingReasons() shouldBe UsingReason.entries.map { reason -> reason.content }
    }

    @DisplayName("회원의 이용사유를 조회한다")
    @Test
    fun findMyUsingReasons_success() {
        // given
        val requests = UsingReason.entries.map { reason -> reason.content }
        memberService.addUsingReasons(requests, member)

        // when
        val result = memberService.findMyUsingReasons(member)

        // then
        result shouldContainExactlyInAnyOrder  requests
    }

    @DisplayName("회원의 이용사유를 삭제한다.")
    @Test
    fun deleteMemberReason_success() {
        // given
        val memberReasons = UsingReason.entries.map { reason -> MemberReason(member.id, reason) }

        // when
        val result = memberReasonRepository.saveAll(memberReasons)
        val deleteId = result[0].id

        // then
        memberService.deleteMemberReason(member, deleteId!!) shouldBe Unit
        memberReasonRepository.findById(deleteId).orElseGet { null } shouldBe null
    }

    @DisplayName("회원의 이용사유 삭제를 실패한다. 회원이 저장하지 않은 이용사유다")
    @Test
    fun deleteMemberReason_fail_not_found() {
        // given
        val memberReasons = UsingReason.entries.map { reason -> MemberReason(member.id, reason) }

        // when
        val result = memberReasonRepository.saveAll(memberReasons)
        val deleteId = result[0].id
        memberReasonRepository.delete(result[0])

        // then
        shouldThrow<MemberReasonNotFoundException> { memberService.deleteMemberReason(member, deleteId!!) }
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
        changedMember.age shouldBe Age.THIRTIES
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