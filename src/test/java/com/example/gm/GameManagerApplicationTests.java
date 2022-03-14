package com.example.gm;

import com.example.gm.dto.QuestionDto;
import com.example.gm.dto.RequestDto;
import com.example.gm.dto.ResponseDtoAnswer;
import com.example.gm.enums.AnswerStatus;
import com.example.gm.exceptions.*;
import com.example.gm.model.Question;
import com.example.gm.services.GmService;
import com.example.gm.services.GmServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest
class GameManagerApplicationTests {
    private GmService gmService;
    private Map<String, Question> questions;

    @BeforeEach
    public void setUp() {
        questions = new HashMap<>();
        gmService = new GmServiceImpl(questions);

    }

    @Test
    void checkWrongAttemptTest() {
        fillQuestions();
        ResponseDtoAnswer actual = gmService.answerQuestion(new RequestDto("Peter", 1, 1, 999));
        then(actual.toString()).isEqualTo(new ResponseDtoAnswer(AnswerStatus.WRONG_ANSWER, 0).toString());
    }

    @Test
    void checkCorrectAttemptTest() {
        fillQuestions();
        int correctAnswer = questions.get("1|2").getIntCorrectAnswer();
        ResponseDtoAnswer actual = gmService.answerQuestion(new RequestDto("Peter", 1, 2, correctAnswer));
        then(actual.toString()).isEqualTo(new ResponseDtoAnswer(AnswerStatus.RIGHT_ANSWER, 1.5).toString());

        correctAnswer = questions.get("1|3").getIntCorrectAnswer();
        actual = gmService.answerQuestion(new RequestDto("Peter", 1, 3, correctAnswer));
        then(actual.toString()).isEqualTo(new ResponseDtoAnswer(AnswerStatus.RIGHT_ANSWER, 2).toString());
    }

    @Test
    void checkGetQuestionFromApi() {
        then(questions.size()).isEqualTo(0);
        RequestDto requestDto = new RequestDto("Peter", 9, 2, 999);
        gmService.answerQuestion(requestDto);
        then(questions.size()).isEqualTo(1);
        then(questions.get("" + requestDto.getGameId() + requestDto.getQuestionId())).isNull();
        then(questions.get(requestDto.getGameId() + "|" + requestDto.getQuestionId())).isNotNull();
    }

    @Test
    void checkGetLeaderBoard() {
        LinkedHashMap<String, Double> results = new LinkedHashMap<>();
        results.put("John", 2.5);
        results.put("Sara", 2.);
        results.put("Anna", 1.5);
        results.put("Peter", 0.);

        playGame();

        then(gmService.getLeaderBoard(1).toString()).isEqualTo(results.toString());
        then(gmService.getLeaderBoard(9)).isEqualTo(Map.of("Peter", 2.));

    }

    private void playGame() {
        fillQuestions();
        int correctAnswer = questions.get("1|1").getIntCorrectAnswer();
        gmService.answerQuestion(new RequestDto("Peter", 1, 1, 999)); //Peter=0
        gmService.answerQuestion(new RequestDto("John", 1, 1, correctAnswer)); //John=1
        correctAnswer = questions.get("1|2").getIntCorrectAnswer();
        gmService.answerQuestion(new RequestDto("Peter", 1, 2, 999)); //Peter=0+0
        gmService.answerQuestion(new RequestDto("Anna", 1, 2, correctAnswer)); //Anna=1.5
        gmService.answerQuestion(new RequestDto("John", 1, 2, correctAnswer));  //John=1+1.5
        correctAnswer = questions.get("1|3").getIntCorrectAnswer();
        gmService.answerQuestion(new RequestDto("Sara", 1, 3, correctAnswer)); //Sara=2
        gmService.answerQuestion(new RequestDto("Anna", 1, 3, 999)); //Anna=1.5+0

        correctAnswer = questions.get("9|1").getIntCorrectAnswer();
        gmService.answerQuestion(new RequestDto("Peter", 9, 1, correctAnswer)); //Peter=2 in gameId=9
    }

    private void fillQuestions() {
        QuestionDto dto = new QuestionDto("French is an official language in Canada.", "True", new String[]{"False"}, "easy");
        questions.put("1|1", GmServiceImpl.dtoToQuestionMapper("1|1", dto));
        QuestionDto dto2 = new QuestionDto("The Great Wall of China is visible from the moon.", "False", new String[]{"True"}, "medium");
        questions.put("1|2", GmServiceImpl.dtoToQuestionMapper("1|2", dto2));
        QuestionDto dto3 = new QuestionDto("Who was the only president to not be in office in Washington D.C?", "George Washington", new String[]{"Abraham Lincoln",
                "Richard Nixon", "Thomas Jefferson"}, "hard");
        questions.put("1|3", GmServiceImpl.dtoToQuestionMapper("1|3", dto3));
        QuestionDto dto4 = new QuestionDto("The sum of all the numbers on a roulette wheel is 666.", "True", new String[]{"False"}, "hard");
        questions.put("9|1", GmServiceImpl.dtoToQuestionMapper("9|1", dto4));
    }


    @Test
    void testGmExceptions_BadRequestExpected() {
        int gameId = 1;
        BadRequestException thrown = Assertions.assertThrows(BadRequestException.class, () -> gmService.getLeaderBoard(gameId));
        Assertions.assertEquals("Bad request, still no games under gameId " + gameId, thrown.getMessage());
    }


    @Test
    void testGmExceptions_DataMissingExceptionExpected() {
        DataMissingException thrown = Assertions.assertThrows(DataMissingException.class, () ->
                gmService.answerQuestion(new RequestDto()));
        Assertions.assertEquals("DataMissingException, Name not provided", thrown.getMessage());
    }

    @Test
    void testGmExceptions_HostNotReachableExceptionExpected() throws IllegalAccessException {
        Field f1 = GmServiceImpl.class.getDeclaredFields()[0];
        f1.setAccessible(true);
        f1.set(GmServiceImpl.class, "WRONG_URL");
        HostNotReachableException thrown = Assertions.assertThrows(HostNotReachableException.class, () ->
                gmService.answerQuestion(new RequestDto("Peter", 9, 1, 999)));
        Assertions.assertEquals("Server not found", thrown.getMessage());
    }

    @Test
    void testGmExceptions_PlayerPermissionDeniedExceptionExpected() {
        fillQuestions();
        String name = "Peter";
        gmService.answerQuestion(new RequestDto(name, 1, 1, 999));
        PlayerPermissionDeniedException thrown = Assertions.assertThrows(PlayerPermissionDeniedException.class, () ->
                gmService.answerQuestion(new RequestDto(name, 1, 1, 999)));
        Assertions.assertEquals("Player: " + name + " can't answer this question, probably, you have already answered it", thrown.getMessage());
    }

    @Test
    void testGmExceptions_WrongGameIdExceptionExpected() {
        WrongGameIdException thrown = Assertions.assertThrows(WrongGameIdException.class, () ->
                gmService.answerQuestion(new RequestDto("Peter", 1, 1, 999)));
        Assertions.assertEquals("Wrong request, may be there no such gameId", thrown.getMessage());
    }

}
