package com.example.gm.model;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.Id;
import java.util.*;

@Getter
@ToString
public class Question {
    @Id
    private final int questionId;
    private final String question;
    private int intCorrectAnswer;
    private final List<String> answers;
    private double points;
    private final Set<String> playersWhichAnswered;

    public Question(int questionId, String question, String correctAnswer, String[] incorrectAnswers, String difficulty) {
        this.questionId = questionId;
        this.question = question;
        answers = new ArrayList<>();
        playersWhichAnswered = new HashSet<>();
        fillAnswers(correctAnswer, incorrectAnswers);
        fillPoints(difficulty);
    }

    private void fillAnswers(String correctAnswer, String[] incorrectAnswers) {
        Random random = new Random();
        intCorrectAnswer = random.nextInt(incorrectAnswers.length);
        answers.addAll(Arrays.asList(incorrectAnswers));
        answers.add(intCorrectAnswer, correctAnswer);

    }

    private void fillPoints(String difficulty) {
        Map<String, Double> mapa = Map.of("easy", 1., "medium", 1.5, "hard", 2.);
        points = mapa.get(difficulty);
    }

    public void addPlayer(String name){
        playersWhichAnswered.add(name);
    }
}
