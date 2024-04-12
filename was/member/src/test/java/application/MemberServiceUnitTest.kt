package application

import org.example.application.MemberService
import org.example.domain.entity.Member
import org.example.domain.enums.Gender
import org.example.domain.repository.MemberRepository
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
        Assertions.assertEquals(result, Unit)
    }

    @DisplayName("회원의 성별을 성공적으로 수정한다.")
    @Test
    fun updateGender() {
        // Given

        // When
        Mockito.`when`(memberRepository.save(member)).thenReturn(member)
        val result = memberService.updateGender("남성", member)

        // Then
        Assertions.assertEquals(result, Unit)
    }
}