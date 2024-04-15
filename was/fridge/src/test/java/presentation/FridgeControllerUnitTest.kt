package presentation

import org.example.application.FridgeService
import org.example.presentation.FridgeController
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class FridgeControllerUnitTest {

    @Mock
    private lateinit var fridgeService: FridgeService

    @InjectMocks
    private lateinit var fridgeController: FridgeController

    @DisplayName("냉장고에 재료를 추가한다.")
    @Test
    fun addIngredient_success() {
        //given

        //when

        //then
    }
}