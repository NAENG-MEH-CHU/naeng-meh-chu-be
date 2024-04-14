package org.example.exception.exceptions

open class BirthNotValidException(type: InputValueType, area: InvalidInputArea):
    RuntimeException("${area.variable}에 대한 입력이 올바르지 않습니다: ${type.type}") {}