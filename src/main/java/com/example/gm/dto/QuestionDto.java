package com.example.gm.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class QuestionDto {
    private String question;
    @JsonAlias("correct_answer")
    private String correctAnswer;
    @JsonAlias("incorrect_answers")
    private String[] incorrectAnswers;
    private String difficulty;

}

