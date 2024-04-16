package application

import io.kotest.matchers.shouldBe
import org.example.application.MemberService
import org.example.domain.entity.Member
import org.example.domain.entity.MemberReason
import org.example.domain.enums.Age
import org.example.domain.enums.Gender
import org.example.domain.enums.UsingReason
import org.example.domain.repository.MemberReasonRepository
import org.example.domain.repository.MemberRepository
import org.example.presentation.dto.request.ChangeAgeRequest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*
import kotlin.collections.ArrayList

@ExtendWith(MockitoExtension::class)
class MemberServiceUnitTest {

    @Mock
    private lateinit var memberRepository: MemberRepository

    @Mock
    private lateinit var memberReasonRepository: MemberReasonRepository

    @InjectMocks
    private lateinit var memberService: MemberService



    private var member = Member.builder()
        .id(UUID.randomUUID())
        .nickname("before")
        .age(null)
        .gender(Gender.MALE)
        .email("test@test.com")
        .ingredients(0)
        .build();

    @DisplayName("등록 가능한 이용 사유를 조회한다")
    @Test
    fun findAllUsingReasons() {
        // given

        // when
        val reasons = memberService.findAllUsingReasons()

        // then
        reasons::class shouldBe ArrayList::class
        reasons[0]::class shouldBe String::class
    }

    @DisplayName("이용 사유들을 저장한다")
    @Test
    fun addUsingReasons() {
        // given
        val reasons = memberService.findAllUsingReasons()

        // when
        Mockito.`when`(memberReasonRepository.saveAll(Mockito.anyList<MemberReason>()))
            .thenReturn(UsingReason.entries.map { each -> MemberReason(member.id, each) })

        // then
        memberService.addUsingReasons(reasons, member) shouldBe Unit
    }

    @DisplayName("내가 저장한 이용 사유들을 조회한다")
    @Test
    fun findMyUsingReasons() {
        // given
        val reasonList = UsingReason.entries.map { each -> MemberReason(member.id, each) }

        // when
        Mockito.`when`(memberReasonRepository.findAllByMemberId(Mockito.any(UUID::class.java)))
            .thenReturn(reasonList)

        // then
        memberService.findMyUsingReasons(member) shouldBe reasonList.map { memberReason -> memberReason.reason.content }
    }

    @DisplayName("내 이용 사유를 삭제한다")
    @Test
    fun deleteMemberReason() {
        // given
        val memberReason = MemberReason(member.id, UsingReason.DIET)

        // when
        Mockito.`when`(memberReasonRepository.findById(Mockito.any(UUID::class.java)))
            .thenReturn(Optional.of(memberReason))
        Mockito.doNothing().`when`(memberReasonRepository).delete(Mockito.any())



        // then
        memberService.deleteMemberReason(member, UUID.randomUUID()) shouldBe Unit
    }

    @DisplayName("회원의 닉네임을 성공적으로 수정한다.")
    @Test
    fun updateNickname() {
        // Given

        // When
        Mockito.`when`(memberRepository.save(member)).thenReturn(member)
        val result = memberService.updateNickname("after", member)

        // Then
        result shouldBe Unit
    }

    @DisplayName("회원의 성별을 성공적으로 수정한다.")
    @Test
    fun updateGender() {
        // Given

        // When
        Mockito.`when`(memberRepository.save(member)).thenReturn(member)
        val result = memberService.updateGender("남성", member)

        // Then
        result shouldBe Unit
    }

    @DisplayName("회원의 출생년도를 성공적으로 수정한다")
    @Test
    fun updateAge() {
        // Given
        val request = ChangeAgeRequest(Age.THIRTIES.type)

        // When
        Mockito.`when`(memberRepository.save(member)).thenReturn(member)
        val result = memberService.updateAge(request, member)

        // Then
        result shouldBe Unit
    }

    @DisplayName("회원을 성공적으로 삭제한다.")
    @Test
    fun deleteMember() {
        // Given

        // When
        Mockito.`when`(memberRepository.existsById(member.id)).thenReturn(true)
        Mockito.doNothing().`when`(memberRepository).delete(member)
        val result = memberService.deleteMember(member)

        // Then
        result shouldBe Unit
    }
}