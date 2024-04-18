package org.example.presentation

import jakarta.validation.Valid
import org.example.application.FridgeService
import org.example.domain.entity.Member
import org.example.presentation.dto.request.AddIngredientRequest
import org.example.support.JwtLogin
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/fridge")
open class FridgeController( private val fridgeService: FridgeService ) {

    @PostMapping("")
    open fun addIngredient(@JwtLogin member: Member, @RequestBody @Valid request: AddIngredientRequest): ResponseEntity<Unit> {
        fridgeService.addIngredient(request, member)
        return ResponseEntity(HttpStatus.CREATED)
    }
}