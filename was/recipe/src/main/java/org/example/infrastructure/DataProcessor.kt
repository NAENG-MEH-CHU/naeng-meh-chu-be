package org.example.infrastructure

import org.example.domain.enums.Age
import org.example.domain.enums.Gender
import org.example.domain.memberRecipe.entity.MemberRecipe
import org.springframework.stereotype.Component

@Component
class DataProcessor() {

    fun transformData(recipe: MemberRecipe): DoubleArray {
        val dataPoint = DoubleArray(2)
        dataPoint[0] = ageToDouble(recipe.memberAge)
        dataPoint[1] = genderToDouble(recipe.gender)

        return dataPoint
    }

    private fun ageToDouble(age: Age): Double {
        return when (age) {
            Age.TEEN -> 0.0
            Age.TWENTIES -> 1.0
            Age.THIRTIES -> 2.0
            Age.FORTIES -> 3.0
            Age.FIFTIES -> 4.0
            Age.SIXTIES -> 5.0
            Age.SEVENTIES -> 6.0
            Age.EIGHTIES -> 7.0
            Age.NINETIES -> 8.0
            else -> throw IllegalArgumentException("Unknown age: $age")
        }
    }

    private fun genderToDouble(gender: Gender): Double {
        return when (gender) {
            Gender.MALE -> 0.0
            Gender.FEMALE -> 1.0
            else -> throw IllegalArgumentException("Unknown gender: $gender")
        }
    }
}