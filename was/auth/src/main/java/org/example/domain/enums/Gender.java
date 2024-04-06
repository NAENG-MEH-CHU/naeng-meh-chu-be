package org.example.domain.enums;

import lombok.Getter;

@Getter
public enum Gender {

    FEMALE("여성"),
    MALE("남성");

    private final String value;

    Gender(String value) {
        this.value = value;
    }

}
