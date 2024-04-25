package application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.equality.shouldBeEqualUsingFields
import io.kotest.matchers.shouldBe
import org.example.FridgeApplication
import org.example.application.FridgeService
import org.example.domain.entity.Member
import org.example.domain.enums.Age
import org.example.domain.enums.Gender
import org.example.domain.fridgeIngredient.entity.FridgeIngredient
import org.example.domain.fridgeIngredient.repository.FridgeIngredientRepository
import org.example.domain.ingredient.entity.Ingredient
import org.example.domain.ingredient.repository.IngredientRepository
import org.example.domain.repository.MemberRepository
import org.example.exception.exceptions.FridgeIngredientForbiddenException
import org.example.exception.exceptions.FridgeIngredientNotFoundException
import org.example.exception.exceptions.IngredientAlreadyInException
import org.example.exception.exceptions.IngredientNotFoundException
import org.example.presentation.dto.request.AddIngredientRequest
import org.example.presentation.dto.response.IngredientsResponse
import org.example.presentation.dto.response.SingleIngredientResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.util.*

@SpringBootTest(classes = [FridgeApplication::class])
@AutoConfigureMockMvc
class FridgeServiceIntegrationTest(
    @Autowired private var memberRepository: MemberRepository,
    @Autowired private var ingredientRepository: IngredientRepository,
    @Autowired private var fridgeIngredientRepository: FridgeIngredientRepository,
    @Autowired private var fridgeService: FridgeService
) {

    private lateinit var member: Member
    private lateinit var ingredient: Ingredient
    private lateinit var other: Member

    @BeforeEach
    fun init() {
        member = memberRepository.save(Member.builder()
            .nickname("before")
            .age(null)
            .gender(Gender.MALE)
            .email("test@test.com")
            .ingredients(0)
            .build())
        other = memberRepository.save(Member.builder()
            .id(UUID.randomUUID())
            .email("other@other.com")
            .nickname("other")
            .age(Age.THIRTIES)
            .ingredients(0)
            .build())
        ingredient = ingredientRepository.save(Ingredient(1, "계란"))
    }

    @DisplayName("냉장고에 재료를 추가한다.")
    @Test
    fun addIngredient_success() {
        // given
        var ingredientId = ingredient.id
        if(ingredientId === null) ingredientId = 1
        val request = AddIngredientRequest(ingredientId, 2017, 3, 1)

        // when
        val result = fridgeService.addIngredient(request, member)

        // then
        result shouldBe Unit
    }

    @DisplayName("이미 존재하는 재료로 냉장고에 재료를 추가하려하면 예외처리한다.")
    @Test
    fun addIngredient_fail_ingredient_already_in() {
        // given
        var ingredientId = ingredient.id
        if(ingredientId === null) ingredientId = 1
        val request = AddIngredientRequest(ingredientId, 2017, 3, 1)
        fridgeService.addIngredient(request, member)

        // when

        // then
        shouldThrow<IngredientAlreadyInException> { fridgeService.addIngredient(request, member) }
    }

    @DisplayName("존재하지 않는 재료로 냉장고에 재료를 추가하려하면 예외처리한다.")
    @Test
    fun addIngredient_fail_ingredient_not_found() {
        // given
        val request = AddIngredientRequest(10000, 2017, 3, 1)

        // when

        // then
        shouldThrow<IngredientNotFoundException> { fridgeService.addIngredient(request, member) }
    }

    @DisplayName("전체 재료를 조회하면 모든 재료를 불러온다")
    @Test
    fun findAllIngredients_success() {
        // given
        val singleResponse = SingleIngredientResponse(ingredient.id, ingredient.name)

        // when

        // then
        fridgeService.findAllIngredients() shouldBeEqualUsingFields IngredientsResponse(listOf(singleResponse))
    }

    @DisplayName("나의 재료 조회를 성공한다")
    @Test
    fun findMyIngredients_success() {
        // given
        val request = AddIngredientRequest(ingredient.id, 2017, 3, 1)
        fridgeService.addIngredient(request, member)

        // when
        val result = fridgeService.findMyIngredients(member)

        // then
        result.myIngredients.size shouldBe 1
    }

    @DisplayName("나의 재료 삭제를 성공한다")
    @Test
    fun deleteMyIngredient_success() {
        // given
        val fridgeIngredient = fridgeIngredientRepository
            .save(FridgeIngredient(member.id, ingredient.id, "계란", LocalDate.now()))

        // when

        // then
        fridgeService.deleteFridgeIngredient(fridgeIngredient.id, member) shouldBe Unit
    }

    @DisplayName("나의 재료 삭제를 실패한다. 재료가 없을 때")
    @Test
    fun deleteMyIngredient_fail_not_found() {
        // given

        // when

        // then
        shouldThrow<FridgeIngredientNotFoundException> {
            fridgeService.deleteFridgeIngredient(UUID.randomUUID(), member)
        }
    }

    @DisplayName("나의 재료 삭제를 실패한다. 권한이 없을 때")
    @Test
    fun deleteMyIngredient_fail_forbidden() {
        // given
        val fridgeIngredient = fridgeIngredientRepository
            .save(FridgeIngredient(member.id, ingredient.id, "계란", LocalDate.now()))


        // when

        // then
        shouldThrow<FridgeIngredientForbiddenException> {
            fridgeService.deleteFridgeIngredient(fridgeIngredient.id, other)
        }
    }

    @AfterEach
    fun afterWork() {
        memberRepository.deleteAll()
        ingredientRepository.deleteAll()
        fridgeIngredientRepository.deleteAll()
    }
}