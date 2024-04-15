package application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.example.application.FridgeService
import org.example.domain.entity.Member
import org.example.domain.enums.Gender
import org.example.domain.event.AddIngredientEvent
import org.example.domain.fridgeIngredient.entity.FridgeIngredient
import org.example.domain.fridgeIngredient.repository.FridgeIngredientRepository
import org.example.domain.ingredient.repository.IngredientRepository
import org.example.exception.exceptions.IngredientAlreadyInException
import org.example.exception.exceptions.IngredientNotFoundException
import org.example.presentation.dto.AddIngredientRequest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.context.ApplicationEventPublisher
import java.time.LocalDate
import java.util.*

@ExtendWith(MockitoExtension::class)
class FridgeServiceUnitTest {

    @Mock
    private lateinit var ingredientRepository: IngredientRepository

    @Mock
    private lateinit var fridgeIngredientRepository: FridgeIngredientRepository

    @Mock
    private lateinit var publisher: ApplicationEventPublisher

    @InjectMocks
    private lateinit var fridgeService: FridgeService

    private var member = Member.builder()
        .id(UUID.randomUUID())
        .nickname("before")
        .age(null)
        .gender(Gender.MALE)
        .email("test@test.com")
        .ingredients(0)
        .build();

    private var fridgeIngredient = FridgeIngredient(member.id, 1, LocalDate.now())

    @DisplayName("회원의 재료 추가에 성공한다.")
    @Test
    fun addIngredient_success() {
        // given
        val request = AddIngredientRequest(1, 2017, 3, 1)

        // when
        Mockito.`when`(ingredientRepository.existsById(request.ingredientId))
            .thenReturn(true)
        Mockito.`when`(fridgeIngredientRepository.save(Mockito.any(FridgeIngredient::class.java)))
            .thenReturn(fridgeIngredient)
        Mockito.doNothing().`when`(publisher).publishEvent(Mockito.any(AddIngredientEvent::class.java))

        // then
        fridgeService.addIngredient(request, member) shouldBe Unit
    }

    @DisplayName("존재하지 않는 재료로 냉장고에 재료 추가를 요청하면 예외처리한다.")
    @Test
    fun addIngredient_fail_ingredient_not_found() {
        // given
        val request = AddIngredientRequest(1, 2017, 3, 1)

        // when
        Mockito.`when`(ingredientRepository.existsById(request.ingredientId))
            .thenReturn(false)

        // then
        shouldThrow<IngredientNotFoundException> { fridgeService.addIngredient(request, member) }
    }

    @DisplayName("이미 존재하는 재료로 재료 추가를 요청하면 예외처리한다.")
    @Test
    fun addIngredient_fail_ingredient_already_in() {
        // given
        val request = AddIngredientRequest(1, 2017, 3, 1)

        // when
        Mockito.`when`(ingredientRepository.existsById(request.ingredientId))
            .thenReturn(true)
        Mockito.`when`(fridgeIngredientRepository.save(Mockito.any(FridgeIngredient::class.java)))
            .thenReturn(fridgeIngredient)
        Mockito.doThrow(IngredientAlreadyInException())
            .`when`(publisher).publishEvent(Mockito.any(AddIngredientEvent::class.java))

        // then
        shouldThrow<IngredientAlreadyInException> { fridgeService.addIngredient(request, member) }
    }
}