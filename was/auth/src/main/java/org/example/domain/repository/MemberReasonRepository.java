package org.example.domain.repository;

import org.example.domain.entity.MemberReason;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MemberReasonRepository extends JpaRepository<MemberReason, UUID> {

    List<MemberReason> findAllByMemberId(UUID memberId);
}
