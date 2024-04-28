package org.example.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor(staticName = "of")
@Getter
public class RemoveIngredientEvent {

    private UUID memberId;
    private int ingredientId;
}
