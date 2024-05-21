package org.example.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class FindMemberReasonsEvent {

    private final UUID memberId;
}
