package org.capstone.was.ingredient.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.capstone.was.common.domain.entity.CommonEntity;
import org.capstone.was.ingredient.domain.enums.MaterialType;

@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Ingredient extends CommonEntity {

    private MaterialType type;

    private String name;
}
