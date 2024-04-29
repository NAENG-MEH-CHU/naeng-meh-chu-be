package application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.equality.shouldBeEqualUsingFields
import io.kotest.matchers.shouldBe
import org.example.application.FridgeEventPublisher
import org.example.application.FridgeService
import org.example.domain.entity.Member
import org.example.domain.enums.Age
import org.example.domain.enums.Gender
import org.example.domain.fridgeIngredient.entity.FridgeIngredient
import org.example.domain.fridgeIngredient.repository.FridgeIngredientRepository
import org.example.domain.ingredient.entity.Ingredient
import org.example.domain.ingredient.repository.IngredientRepository
import org.example.exception.exceptions.FridgeIngredientForbiddenException
import org.example.exception.exceptions.FridgeIngredientNotFoundException
import org.example.exception.exceptions.IngredientAlreadyInException
import org.example.exception.exceptions.IngredientNotFoundException
import org.example.presentation.dto.request.AddIngredientRequest
import org.example.presentation.dto.response.IngredientsResponse
import org.example.presentation.dto.response.MyIngredientsResponse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate
import java.util.*

@ExtendWith(MockitoExtension::class)
class FridgeServiceUnitTest {

    @Mock
    private lateinit var ingredientRepository: IngredientRepository

    @Mock
    private lateinit var fridgeIngredientRepository: FridgeIngredientRepository

    @Mock
    private lateinit var publisher: FridgeEventPublisher

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
    private var ingredient = Ingredient(1, "test")
    private var fridgeIngredient = FridgeIngredient(member.id, 1, "test", LocalDate.now())

    @DisplayName("회원의 재료 추가에 성공한다.")
    @Test
    fun addIngredient_success() {
        // given
        val request = AddIngredientRequest(1, 2017, 3, 1)

        // when
        Mockito.`when`(ingredientRepository.findById(request.ingredientId))
            .thenReturn(Optional.of(ingredient))
        Mockito.`when`(fridgeIngredientRepository.existsByIngredientIdAndMemberId(request.ingredientId!!, member.id))
            .thenReturn(false)
        Mockito.`when`(fridgeIngredientRepository.save(Mockito.any(FridgeIngredient::class.java)))
            .thenReturn(fridgeIngredient)
        Mockito.doNothing().`when`(publisher).addIngredient(member.id, ingredient.id)

        // then
        fridgeService.addIngredient(request, member) shouldBe Unit
    }

    @DisplayName("존재하지 않는 재료로 냉장고에 재료 추가를 요청하면 예외처리한다.")
    @Test
    fun addIngredient_fail_ingredient_not_found() {
        // given
        val request = AddIngredientRequest(1, 2017, 3, 1)

        // when

        Mockito.`when`(ingredientRepository.findById(request.ingredientId))
            .thenReturn(Optional.empty())
        // then
        shouldThrow<IngredientNotFoundException> { fridgeService.addIngredient(request, member) }
    }

    @DisplayName("이미 존재하는 재료로 재료 추가를 요청하면 예외처리한다.")
    @Test
    fun addIngredient_fail_ingredient_already_in() {
        // given
        val request = AddIngredientRequest(1, 2017, 3, 1)

        // when
        Mockito.`when`(ingredientRepository.findById(request.ingredientId))
            .thenReturn(Optional.of(ingredient))
        Mockito.`when`(fridgeIngredientRepository.existsByIngredientIdAndMemberId(request.ingredientId!!, member.id))
            .thenReturn(true)

        // then
        shouldThrow<IngredientAlreadyInException> { fridgeService.addIngredient(request, member) }
    }

    @DisplayName("전체 재료를 조회한다")
    @Test
    fun findAllIngredients_success() {
        // given

        // when
        Mockito.`when`(ingredientRepository.findAll())
            .thenReturn(listOf())

        // then
        fridgeService.findAllIngredients() shouldBeEqualUsingFields IngredientsResponse(listOf())
    }

    @DisplayName("나의 재료들을 조회한다")
    @Test
    fun findMyIngredients_success() {
        // given

        // when
        Mockito.`when`(fridgeIngredientRepository.findAllByMemberId(member.id))
            .thenReturn(listOf())

        // then
        fridgeService.findMyIngredients(member)::class shouldBe MyIngredientsResponse::class
    }

    @DisplayName("유통기한 임박 재료들을 조회한다")
    @Test
    fun findUpcomingIngredients_success() {
        // given

        // when
        Mockito.`when`(fridgeIngredientRepository.findFridgeIngredientsExpiresWithin(LocalDate.now(), member.id))
            .thenReturn(listOf())

        // then
        fridgeService.findUpcomingIngredients(member, 0L)::class shouldBe MyIngredientsResponse::class
    }

    @DisplayName("나의 재료 삭제를 성공한다")
    @Test
    fun deleteFridgeIngredient_success() {
        // given

        // when
        Mockito.`when`(fridgeIngredientRepository.findById(fridgeIngredient.id))
            .thenReturn(Optional.of(fridgeIngredient))

        // then
        fridgeService.deleteFridgeIngredient(fridgeIngredient.id, member) shouldBe Unit
    }

    @DisplayName("나의 재료 삭제를 실패한다. 데이터가 없을 때")
    @Test
    fun deleteFridgeIngredient_not_found() {
        // given

        // when
        Mockito.`when`(fridgeIngredientRepository.findById(fridgeIngredient.id))
            .thenReturn(Optional.empty())

        // then
        shouldThrow<FridgeIngredientNotFoundException> {
            fridgeService.deleteFridgeIngredient(fridgeIngredient.id, member)
        }
    }

    @DisplayName("나의 재료 삭제를 실패한다. member의 재료가 아닐 때")
    @Test
    fun deleteFridgeIngredient_forbidden() {
        // given
        val other = Member.builder()
            .id(UUID.randomUUID())
            .email("other@other.com")
            .nickname("other")
            .age(Age.THIRTIES)
            .ingredients(0)
            .build()

        // when
        Mockito.`when`(fridgeIngredientRepository.findById(fridgeIngredient.id))
            .thenReturn(Optional.of(fridgeIngredient))

        // then
        shouldThrow<FridgeIngredientForbiddenException> {
            fridgeService.deleteFridgeIngredient(fridgeIngredient.id, other)
        }
    }
}