package org.example.domain.repository

import org.example.domain.entity.MemberReason
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface MemberReasonRepository: JpaRepository<MemberReason, UUID> {

    fun findAllByMemberId(memberId: UUID): List<MemberReason>
}