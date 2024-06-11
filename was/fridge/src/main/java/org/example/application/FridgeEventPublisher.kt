package org.example.application

import lombok.RequiredArgsConstructor
import org.example.domain.event.AddIngredientEvent
import org.example.domain.event.RemoveIngredientEvent
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.UUID


@Service
@RequiredArgsConstructor
class FridgeEventPublisher(
    private val publisher: ApplicationEventPublisher,
) {

    fun addIngredient(memberId: UUID, ingredientId: Int) {
        println("add ingredient publisher called")
        publisher.publishEvent(AddIngredientEvent.of(memberId, ingredientId))
    }

    fun removeIngredient(memberId: UUID, ingredientId: Int) {
        println("remove ingredient publisher called")
        publisher.publishEvent(RemoveIngredientEvent.of(memberId, ingredientId))
    }
}