package org.example.application;

import lombok.RequiredArgsConstructor;
import org.example.domain.entity.MemberReason;
import org.example.domain.enums.UsingReason;
import org.example.domain.repository.MemberReasonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberReasonService {

    private final MemberReasonRepository memberReasonRepository;

    @Transactional(readOnly = true)
    public boolean hasMemberReason(final UUID memberId) {
        return !memberReasonRepository.findAllByMemberId(memberId).isEmpty();
    }

    @Transactional
    public void saveMemberReasons(final UUID memberId, final List<UsingReason> reasons) {
        memberReasonRepository.saveAll(reasons.stream()
                        .map(each -> new MemberReason(memberId, each))
                .collect(Collectors.toList()));
    }
}
