package org.example.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.domain.enums.Age;
import org.example.domain.enums.Gender;
import org.example.domain.enums.UsingReason;
import org.example.support.NotBlankList;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class InitializeMemberRequest {

    @NotBlank(message = "수정할 닉네임을 입력해주세요")
    private String nickname;

    @NotBlank(message = "성별을 입력해주세요")
    private String gender;

    @NotBlank(message = "나이대를 입력해주세요")
    private String age;

    @NotNull
    @NotBlankList
    private List<String> usingReasons;

    public Age paresAge() {
        for(Age each : Age.values()) {
            if(each.getType().equals(age)) return each;
        }

        return null;
    }

    public Gender parseGender() {
        if(Gender.MALE.getValue().equals(gender)) return Gender.MALE;
        return Gender.FEMALE;
    }

    public List<UsingReason> parseReasons() {
        return usingReasons.stream().map(UsingReason::parseReason).toList();
    }
}
