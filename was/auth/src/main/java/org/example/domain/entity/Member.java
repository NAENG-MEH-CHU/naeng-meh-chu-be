package org.example.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.domain.enums.Age;
import org.example.domain.enums.Gender;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Getter
@Entity
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String email;

    @Column
    private String nickname;

    @Lob
    private String ingredients;

    @Column
    @Enumerated(EnumType.STRING)
    private Age age;

    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;

    public boolean containsIngredient(final int value) {
        return ingredients.charAt(value-1) == '1';
    }

    public void addIngredient(final int value) {
        if(ingredients.length() < value) {
            // "10111"일 때 8을 추가하는 거라면? -> "10111001"이 되어야 한다.
            // 기존 길이 5, 새로운 길이 8
            // 길이차이 = 3, 마지막은 1이어야 한다.
            // 길이차 -1 만큼 0을 채우고 마지막에 1 추가
            String adder = "0".repeat(value - 1 - ingredients.length()) + "1";
            ingredients+= adder;
            return;
        }
        // "10111"에서 2를 추가하는 경우는?
        // 문자열의 배열로 만든 다음, value-1인덱스의 값을 "1"로 바꾸고 join시키자.
        // 결과는 "11111"이다.
        char[] chars = ingredients.toCharArray();
        chars[value - 1] = '1';
        ingredients = new String(chars);
    }

    public void removeIngredient(final int value) {
        if(ingredients.length() < value) {
            return;
        }
        char[] chars = ingredients.toCharArray();
        chars[value - 1] = '0';
        ingredients = new String(chars);
    }

    public void updateNickname(final String nickname) {
        this.nickname = nickname;
    }

    public void updateGender(final Gender gender) {
        this.gender = gender;
    }

    public void updateAge(final Age age) {
        this.age = age;
    }

    public boolean isFinishedOnboarding() {
        return !(nickname != null && age != null && gender != null);
    }
}