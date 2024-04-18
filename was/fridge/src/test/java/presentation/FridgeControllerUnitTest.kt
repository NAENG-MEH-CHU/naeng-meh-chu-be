package presentation

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.example.application.FridgeService
import org.example.domain.entity.Member
import org.example.domain.enums.Gender
import org.example.exception.exceptions.IngredientNotFoundException
import org.example.presentation.FridgeController
import org.example.presentation.dto.AddIngredientRequest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@ExtendWith(MockitoExtension::class)
class FridgeControllerUnitTest {

    @Mock
    private lateinit var fridgeService: FridgeService

    @InjectMocks
    private lateinit var fridgeController: FridgeController

    private val member = Member.builder()
        .nickname("before")
        .age(null)
        .gender(Gender.MALE)
        .email("test@test.com")
        .ingredients(0)
        .build();

    @DisplayName("냉장고에 재료를 추가한다.")
    @Test
    fun addIngredient_success() {
        //given
        val request = AddIngredientRequest(1, 2017, 3, 1)

        //when
        Mockito.doNothing().`when`(fridgeService).addIngredient(request, member)


        //then
        fridgeController.addIngredient(member, request) shouldBe ResponseEntity(HttpStatus.CREATED)
    }

    @DisplayName("냉장고에 재료 추가를 실패한다. 재료가 없을 시.")
    @Test
    fun addIngredient_fail_invalid() {
        //given
        val request = AddIngredientRequest(1, 2017, 3, 1)

        //when
        Mockito.doThrow(IngredientNotFoundException()).`when`(fridgeService).addIngredient(request, member)

        //then
        shouldThrow<IngredientNotFoundException> { fridgeController.addIngredient(member, request) }
    }
}