package org.example.application;

import lombok.RequiredArgsConstructor;
import org.example.domain.entity.Member;
import org.example.domain.event.AddIngredientEvent;
import org.example.domain.event.RemoveIngredientEvent;
import org.example.domain.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class MemberEventHandler {

    private final MemberRepository memberRepository;

    @TransactionalEventListener(AddIngredientEvent.class)
    public void addIngredient(final AddIngredientEvent event) {
        Member member = event.getMember();
        int ingredientId = event.getIngredientId();
        if(member.containsIngredient(ingredientId)) return;
        member.addIngredient(ingredientId);
        memberRepository.save(member);
    }

    @TransactionalEventListener(RemoveIngredientEvent.class)
    public void removeIngredient(final RemoveIngredientEvent event) {
        Member member = event.getMember();
        int ingredientId = event.getIngredientId();
        if(member.containsIngredient(ingredientId)) {
            member.addIngredient(ingredientId);
            memberRepository.save(member);
        }
    }
}
