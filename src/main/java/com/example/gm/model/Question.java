package com.example.gm.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.*;

@Getter
@ToString
@EqualsAndHashCode(of = {"uniqueId"})
public class Question {
    private final String uniqueId;
    private final String question;
    private final int intCorrectAnswer;
    private final List<String> answers;
    private final Set<String> playersWhichAnswered;
    private double points;

    public Question(String uniqueId, String question, String correctAnswer, String[] incorrectAnswers, String difficulty) {
        this.uniqueId = uniqueId;
        this.question = question;
        intCorrectAnswer = new Random().nextInt(incorrectAnswers.length);
        answers = new ArrayList<>();
        playersWhichAnswered = new HashSet<>();
        fillAnswers(correctAnswer, incorrectAnswers);
        fillPoints(difficulty);
    }

    private void fillAnswers(String correctAnswer, String[] incorrectAnswers) {
        answers.addAll(Arrays.asList(incorrectAnswers));
        answers.add(intCorrectAnswer, correctAnswer);

    }

    private void fillPoints(String difficulty) {
        Map<String, Double> map = Map.of("easy", 1., "medium", 1.5, "hard", 2.);
        points = map.get(difficulty);
    }

    public void addPlayer(String name){
        playersWhichAnswered.add(name);
    }


}
