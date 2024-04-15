package org.example.domain.repository;

import org.example.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {

    Optional<Member> findByEmail(String email);
    boolean existsById(UUID id);
    boolean existsByEmail(String email);
}
