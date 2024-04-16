package org.example.domain.entity

import jakarta.persistence.*
import org.example.domain.enums.UsingReason
import java.util.UUID

@Entity
@Table(name = "MemberReason")
open class MemberReason{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID?

    @Column(nullable = false)
    val memberId: UUID

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    val reason: UsingReason

    protected constructor(id: UUID?, memberId: UUID, reason: UsingReason) {
        this.memberId = memberId
        this.reason = reason
        this.id = id
    }

    constructor(memberId: UUID, reason: UsingReason): this(null, memberId, reason) {}

    protected constructor() : this(null, UUID.randomUUID(), UsingReason.DIET) {}
}