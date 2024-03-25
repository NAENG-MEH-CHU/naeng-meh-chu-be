package org.capstone.was.ingredient.domain.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.capstone.was.ingredient.domain.enums.MaterialType;

@Entity
@NoArgsConstructor
@Getter
public class Sauce extends Ingredient {

    public Sauce(MaterialType type, String name) {
        super(type, name);
    }

    public static Sauce from(final MaterialType type, final String name) {
        return new Sauce(type, name);
    }
}
