package org.example.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.domain.enums.Age;
import org.example.domain.enums.Gender;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

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

    @Column
    private long ingredients;

    @Column
    @Enumerated(EnumType.STRING)
    private Age age;

    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;

    public boolean containsIngredient(final int value) {
        return (ingredients & value) == value;
    }

    public void addIngredient(final int value) {
        ingredients |= value;
    }

    public void removeIngredient(final int value) {
        ingredients &= ~value;
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
        return (nickname != null && age != null && gender != null);
    }
}