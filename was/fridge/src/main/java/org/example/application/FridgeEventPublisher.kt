package org.example.application

import lombok.RequiredArgsConstructor
import org.example.domain.event.AddIngredientEvent
import org.example.domain.event.RemoveIngredientEvent
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.UUID


@Service
@RequiredArgsConstructor
class FridgeEventPublisher(
    @Qualifier("removeIngredientTemplate") private val removeIngredientTemplate: RedisTemplate<String, Any>,
    @Qualifier("addIngredientTemplate") private val addIngredientTemplate: RedisTemplate<String, Any>
) {

    private val ADD_CHANNEL = "AddIngredient"
    private val REMOVE_CHANNEL = "RemoveIngredient"

    fun addIngredient(memberId: UUID, ingredientId: Int) {
        addIngredientTemplate.convertAndSend(ADD_CHANNEL, AddIngredientEvent.of(memberId, ingredientId))
    }

    fun removeIngredient(memberId: UUID, ingredientId: Int) {
        removeIngredientTemplate.convertAndSend(REMOVE_CHANNEL, RemoveIngredientEvent.of(memberId, ingredientId))
    }
}