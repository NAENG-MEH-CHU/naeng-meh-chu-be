package org.example.application;

import lombok.RequiredArgsConstructor;
import org.example.domain.repository.MemberReasonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberReasonService {

    private final MemberReasonRepository memberReasonRepository;

    @Transactional(readOnly = true)
    public boolean hasMemberReason(final UUID memberId) {
        return !memberReasonRepository.findAllByMemberId(memberId).isEmpty();
    }
}
