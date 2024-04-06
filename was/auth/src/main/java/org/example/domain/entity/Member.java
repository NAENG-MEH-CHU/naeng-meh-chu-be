package org.example.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.domain.enums.Gender;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
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
    private int age;

    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;

    private void addIngredient(final int value) {
        convertIngredientTo(getChangingIndex(value), "1");
    }

    private void removeIngredient(final int value) {
        convertIngredientTo(getChangingIndex(value), "0");
    }

    private void convertIngredientTo(final int index, final String target) {
        ingredients = ingredients.substring(0, index-1)
                + target
                + ingredients.substring(index+1);
    }

    private int getChangingIndex(final int value) {
        return ingredients.length() - value;
    }

    private void updateNickname(final String nickname) {
        this.nickname = nickname;
    }
}
