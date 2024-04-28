package org.example.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.domain.entity.Member;
import org.example.domain.event.AddIngredientEvent;
import org.example.domain.event.RemoveIngredientEvent;
import org.example.domain.repository.MemberRepository;
import org.example.exception.exceptions.MemberNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class MemberEventHandler implements MessageListener {

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MemberRepository memberRepository;

    @Transactional
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
        handleMessageForIngredient(messageBody);
    }

    public void handleMessageForIngredient(String messageBody) {
        try {
            Object eventObject = mapper.readValue(messageBody, Object.class);

            if (eventObject instanceof AddIngredientEvent addIngredientEvent) {
                addIngredient(addIngredientEvent);
                return;
            }
            if (eventObject instanceof RemoveIngredientEvent removeIngredientEvent) {
                removeIngredient(removeIngredientEvent);
            }
        } catch (Exception e) {
            // 예외 처리
            e.printStackTrace();
        }
    }

    public void addIngredient(AddIngredientEvent event) {
        Member member = memberRepository.findById(event.getMemberId()).orElseThrow(MemberNotFoundException::new);
        if(!member.containsIngredient(event.getIngredientId())){
            member.addIngredient(event.getIngredientId());
            memberRepository.save(member);
        }
    }

    public void removeIngredient(RemoveIngredientEvent event) {
        Member member = memberRepository.findById(event.getMemberId()).orElseThrow(MemberNotFoundException::new);
        if(member.containsIngredient(event.getIngredientId())){
            member.removeIngredient(event.getIngredientId());
            memberRepository.save(member);
        }
    }
}
