package org.example.domain.enums;

public enum Age {

    TEEN(10, "10대"),
    TWENTIES(20, "20대"),
    THIRTIES(30, "30대"),
    FORTIES(40, "40대"),
    FIFTIES(50, "50대"),
    SIXTIES(60, "60대"),
    SEVENTIES(70, "70대"),
    EIGHTIES(80, "80대"),
    NINETIES(90, "90대");

    private int numericValue;
    private String type;

    Age(int numericValue, String type) {
        this.numericValue = numericValue;
        this.type = type;
    }

    public int getNumericValue() {
        return numericValue;
    }

    public String getType() {
        return type;
    }

    public static Age getAgeByType(String type) {
        if(type.equals(TEEN.type)) return TEEN;
        if(type.equals(TWENTIES.type)) return TWENTIES;
        if(type.equals(THIRTIES.type)) return THIRTIES;
        if(type.equals(FORTIES.type)) return FORTIES;
        if(type.equals(FIFTIES.type)) return FIFTIES;
        if(type.equals(SIXTIES.type)) return SIXTIES;
        if(type.equals(SEVENTIES.type)) return SEVENTIES;
        if(type.equals(EIGHTIES.type)) return EIGHTIES;
        if(type.equals(NINETIES.type)) return NINETIES;
        return null;
    }
}
