package org.capstone.was.ingredient.domain.enums;

public enum MaterialType {

    SOLID("고체"),
    LIQUID("액체");

    private final String name;

    MaterialType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
