package com.example.gm.dto;

import com.example.gm.AnswerStatus;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ResponseDtoAnswer {
    private AnswerStatus answerStatus = AnswerStatus.WRONG_ANSWER;
    private double pointsEarned;


}
