package com.example.gm.dto;

import com.example.gm.enums.AnswerStatus;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ResponseDtoAnswer {
    private AnswerStatus answerStatus = AnswerStatus.WRONG_ANSWER;
    private double pointsEarned;


}
