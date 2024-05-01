package org.example.domain.memberRecipe.repository

import org.example.domain.memberRecipe.entity.MemberRecipe
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface MemberRecipeRepository: JpaRepository<MemberRecipe, UUID> {

    fun findAllByMemberId(memberId: UUID): List<MemberRecipe>
}