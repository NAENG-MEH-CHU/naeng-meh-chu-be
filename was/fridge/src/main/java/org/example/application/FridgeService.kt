package org.example.application

import lombok.RequiredArgsConstructor
import org.example.domain.entity.Member
import org.example.domain.event.AddIngredientEvent
import org.example.domain.fridgeIngredient.entity.FridgeIngredient
import org.example.domain.fridgeIngredient.repository.FridgeIngredientRepository
import org.example.domain.ingredient.repository.IngredientRepository
import org.example.exception.exceptions.IngredientAlreadyInException
import org.example.exception.exceptions.IngredientNotFoundException
import org.example.presentation.dto.request.AddIngredientRequest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@RequiredArgsConstructor
open class FridgeService(
    private val ingredientRepository: IngredientRepository,
    private val fridgeIngredientRepository: FridgeIngredientRepository,
    private val publisher: ApplicationEventPublisher
) {

    @Transactional
    open fun addIngredient(request: AddIngredientRequest, member: Member) {
        if(!ingredientRepository.existsById(request.ingredientId!!)) {
            throw IngredientNotFoundException()
        }
        validateExistence(member, request.ingredientId)

        val date = LocalDate.of(request.year!!, request.month!!, request.day!!)
        val fridgeIngredient = FridgeIngredient(member.id, request.ingredientId, date)
        fridgeIngredientRepository.save(fridgeIngredient)
        publisher.publishEvent(AddIngredientEvent.of(member, request.ingredientId))
    }

    private fun validateExistence(member: Member, ingredientId: Int) {
        if(memberContainsIngredient(member, ingredientId)) throw IngredientAlreadyInException()
    }

    private fun memberContainsIngredient(member: Member, ingredientId: Int): Boolean {
        return fridgeIngredientRepository.existsByIngredientIdAndMemberId(ingredientId, member.id)
    }
}