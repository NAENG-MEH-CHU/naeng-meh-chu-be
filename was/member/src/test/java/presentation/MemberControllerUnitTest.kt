package presentation

import io.kotest.matchers.shouldBe
import org.example.application.MemberService
import org.example.domain.entity.Member
import org.example.domain.enums.Gender
import org.example.presentation.MemberController
import org.example.presentation.dto.ChangeGenderRequest
import org.example.presentation.dto.ChangeNicknameRequest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@ExtendWith(MockitoExtension::class)
class MemberControllerUnitTest {

    @Mock
    private lateinit var memberService: MemberService

    @InjectMocks
    private lateinit var memberController: MemberController

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
        val nickname = "after"

        // When
        Mockito
                .doNothing()
                .`when`(memberService)
                .updateNickname(nickname, member)
        val result = memberController.updateNickname(member, ChangeNicknameRequest(nickname))

        // Then
        result shouldBe ResponseEntity<Unit>(HttpStatus.OK)
    }

    @DisplayName("회원의 성별을 성공적으로 수정한다.")
    @Test
    fun updateGender() {
        // Given
        val gender = "여성"

        // When
        Mockito
            .doNothing()
            .`when`(memberService)
            .updateGender(gender, member)
        val result = memberController.updateGender(member, ChangeGenderRequest(gender))

        // Then
        result shouldBe ResponseEntity<Unit>(HttpStatus.OK)
    }

    @DisplayName("회원을 성공적으로 삭제한다.")
    @Test
    fun deleteMember() {
        // Given

        // When
        Mockito
            .doNothing()
            .`when`(memberService)
            .deleteMember(member)
        val result = memberController.deleteMember(member)

        // Then
        result shouldBe ResponseEntity<Unit>(HttpStatus.NO_CONTENT)
    }
}