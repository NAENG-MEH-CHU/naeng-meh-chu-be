package org.example.presentation

import jakarta.validation.Valid
import org.example.application.FridgeService
import org.example.domain.entity.Member
import org.example.presentation.dto.request.AddIngredientRequest
import org.example.presentation.dto.response.IngredientsResponse
import org.example.presentation.dto.response.MyIngredientsResponse
import org.example.support.JwtLogin
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/fridge")
class FridgeController( private val fridgeService: FridgeService ) {

    @PostMapping("")
    fun addIngredient(@JwtLogin member: Member, @RequestBody @Valid request: AddIngredientRequest): ResponseEntity<Unit> {
        fridgeService.addIngredient(request, member)
        return ResponseEntity(HttpStatus.CREATED)
    }

    @GetMapping("")
    fun findAllIngredients(@JwtLogin member: Member): ResponseEntity<IngredientsResponse> {
        return ResponseEntity(fridgeService.findAllIngredients(), HttpStatus.OK)
    }

    @GetMapping("mine")
    fun findMyIngredients(@JwtLogin member: Member): ResponseEntity<MyIngredientsResponse> {
        return ResponseEntity(fridgeService.findMyIngredients(member), HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun deleteMyIngredient(@JwtLogin member: Member, @PathVariable("id") id: String): ResponseEntity<Unit> {
        val fridgeIngredientId = UUID.fromString(id)
        fridgeService.deleteFridgeIngredient(fridgeIngredientId, member)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}