package org.example.domain.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum UsingReason {
    DIET("다이어트를 하는데 집에 있는 음식들로 식단을 관리하고 싶어요."),
    VARIETY("좀 더 다양한 음식을 해 먹고 싶어요."),
    LESS_TRASH("유통기한이 임박한 재료들로 요리를 해서 버려지는 음식을 줄이고 싶어요."),
    HEALTH("몸에 좋은 건강한 레시피를 추천받고 싶어요.");

    private final String content;
    private static final Map<String, UsingReason> CONTENT_TO_REASON_MAP = new HashMap<>();

    static {
        for (UsingReason reason : UsingReason.values()) {
            CONTENT_TO_REASON_MAP.put(reason.getContent(), reason);
        }
    }

    UsingReason(String content) {
        this.content = content;
    }

    public static UsingReason parseReason(final String content) {
        return CONTENT_TO_REASON_MAP.get(content);
    }
}
