package org.example.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.domain.entity.Member;

@AllArgsConstructor(staticName = "of")
@Getter
public class RemoveIngredientEvent {

    private Member member;
    private int ingredientId;
}
