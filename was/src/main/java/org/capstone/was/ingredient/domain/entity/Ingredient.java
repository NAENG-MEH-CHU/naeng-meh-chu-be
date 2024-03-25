package org.capstone.was.ingredient.domain.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import org.capstone.was.common.domain.entity.CommonEntity;
import org.capstone.was.ingredient.domain.enums.MaterialType;

@MappedSuperclass
@AllArgsConstructor(staticName = "of")
public class Ingredient extends CommonEntity {

    private MaterialType type;

    private String name;

    private double amount;

    public void decreaseAmount(final double amount) {
        this.amount -= amount;
    }
}
