package org.example.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.domain.enums.UsingReason;

import java.util.UUID;

@Entity
@Table(name = "MemberReason")
@NoArgsConstructor
@Getter
public class MemberReason {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID memberId;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UsingReason reason;

    protected MemberReason(UUID id, UUID memberId, UsingReason reason) {
        this.memberId = memberId;
        this.reason = reason;
        this.id = id;
    }

    public MemberReason(UUID memberId, UsingReason reason) {
        this.memberId = memberId;
        this.reason = reason;
    }
}
