package org.capstone.was.ingredient.domain.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.capstone.was.ingredient.domain.enums.MaterialType;

@Entity
@Getter
@NoArgsConstructor
public class CookingIngredient extends Ingredient{

    public CookingIngredient(final MaterialType type, final String name) {
        super(type, name);
    }

    public static CookingIngredient from(final MaterialType type, final String name) {
        return new CookingIngredient(type, name);
    }
}
