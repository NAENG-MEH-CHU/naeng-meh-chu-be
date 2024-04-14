package application

import io.kotest.matchers.shouldBe
import org.example.application.MemberService
import org.example.domain.entity.Member
import org.example.domain.enums.Gender
import org.example.domain.repository.MemberRepository
import org.example.exception.exceptions.MemberNotFoundException
import org.example.presentation.dto.ChangeBirthRequest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class MemberServiceUnitTest {

    @Mock
    private lateinit var memberRepository: MemberRepository

    @InjectMocks
    private lateinit var memberService: MemberService

    private var member = Member.builder()
            .nickname("before")
            .age(null)
            .gender(Gender.MALE)
            .email("test@test.com")
            .ingredients(0)
            .build();

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
        val request = ChangeBirthRequest(1998, 9, 5)

        // When
        Mockito.`when`(memberRepository.save(member)).thenReturn(member)
        val result = memberService.updateBirth(request, member)

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