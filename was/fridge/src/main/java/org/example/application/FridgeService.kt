package org.example.application

import lombok.RequiredArgsConstructor
import org.example.domain.entity.Member
import org.example.domain.event.AddIngredientEvent
import org.example.domain.fridgeIngredient.entity.FridgeIngredient
import org.example.domain.fridgeIngredient.repository.FridgeIngredientRepository
import org.example.domain.ingredient.repository.IngredientRepository
import org.example.exception.exceptions.FridgeIngredientNotFoundException
import org.example.exception.exceptions.IngredientAlreadyInException
import org.example.exception.exceptions.IngredientNotFoundException
import org.example.presentation.dto.request.AddIngredientRequest
import org.example.presentation.dto.response.IngredientsResponse
import org.example.presentation.dto.response.MyIngredientsResponse
import org.example.presentation.dto.response.SingleIngredientResponse
import org.example.presentation.dto.response.SingleMyIngredientResponse
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.UUID

@Service
@RequiredArgsConstructor
open class FridgeService(
    private val ingredientRepository: IngredientRepository,
    private val fridgeIngredientRepository: FridgeIngredientRepository,
    private val publisher: ApplicationEventPublisher
) {

    @Transactional
    open fun addIngredient(request: AddIngredientRequest, member: Member) {

        val ingredient = ingredientRepository.findById(request.ingredientId!!)
            .orElseThrow { IngredientNotFoundException() }
        validateExistence(member, ingredient.id)

        val date = LocalDate.of(request.year!!, request.month!!, request.day!!)
        val fridgeIngredient = FridgeIngredient(member.id, ingredient.id, ingredient.name, date)
        fridgeIngredientRepository.save(fridgeIngredient)
        publisher.publishEvent(AddIngredientEvent.of(member, request.ingredientId))
    }

    @Transactional(readOnly = true)
    open fun findAllIngredients(): IngredientsResponse {
        return IngredientsResponse(ingredientRepository.findAll()
            .map{ ingredient -> SingleIngredientResponse(ingredient.id, ingredient.name) })
    }

    @Transactional(readOnly = true)
    open fun findMyIngredients(member: Member): MyIngredientsResponse {
        val myIngredients =  fridgeIngredientRepository.findAllByMemberId(member.id)
            .map{ fridgeIngredient -> SingleMyIngredientResponse(fridgeIngredient) }
        return MyIngredientsResponse(myIngredients)
    }

    @Transactional
    open fun deleteFridgeIngredient(fridgeIngredientId: UUID, member: Member) {

    }

    private fun validateExistence(member: Member, ingredientId: Int) {
        if(memberContainsIngredient(member, ingredientId)) throw IngredientAlreadyInException()
    }

    private fun memberContainsIngredient(member: Member, ingredientId: Int): Boolean {
        return fridgeIngredientRepository.existsByIngredientIdAndMemberId(ingredientId, member.id)
    }
}