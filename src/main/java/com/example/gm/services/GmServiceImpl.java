package com.example.gm.services;

import com.example.gm.enums.AnswerStatus;
import com.example.gm.dto.QuestionDto;
import com.example.gm.dto.RequestDto;
import com.example.gm.dto.ResponseDto;
import com.example.gm.dto.ResponseDtoAnswer;
import com.example.gm.exceptions.HostException;
import com.example.gm.exceptions.PlayerPermissionDenied;
import com.example.gm.model.Player;
import com.example.gm.model.Question;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class GmServiceImpl implements GmService {
    private static final String OPENT_DB = "https://opentdb.com/api.php?amount=1";
    private static final RestTemplate restTemplate = new RestTemplate();
    private final Map<Integer, Question> questions = new HashMap<>();
    private final Map<String, Player> players = new HashMap<>();

    @Override
    public ResponseDtoAnswer getResponse(RequestDto requestDto) {
        if (!players.containsKey(requestDto.getUserName()))
            registerNewPlayer(requestDto.getUserName());
        if (checkIfPlayerCanPlay(requestDto))
            throw new PlayerPermissionDenied(requestDto.getUserName());
        Question question = questions.getOrDefault(requestDto.getQuestionId(), getQuestionFromApi(requestDto));
        //addLock

        ResponseDtoAnswer responseDtoAnswer = new ResponseDtoAnswer();
        if (requestDto.getAnswerId() == question.getIntCorrectAnswer()) {
            responseDtoAnswer.setAnswerStatus(AnswerStatus.RIGHT_ANSWER);
            responseDtoAnswer.setPointsEarned(question.getPoints());
            players.get(requestDto.getUserName()).setPoints(requestDto.getGameId(), question.getPoints());

        } else
            players.get(requestDto.getUserName()).setPoints(requestDto.getGameId(), 0);
        System.out.println("player results: " + players.get("Vasya").getResults());
        return responseDtoAnswer;
    }

    private void registerNewPlayer(String userName) {
        players.put(userName, new Player(userName));
    }

    private boolean checkIfPlayerCanPlay(RequestDto requestDto) {
        //TODO
        return false;
    }

    private Question getQuestionFromApi(RequestDto requestDto) {
        QuestionDto dto = null;
        try {
            RequestEntity<String> requestEntity = new RequestEntity<>(HttpMethod.GET, new URI(OPENT_DB));
            ResponseEntity<ResponseDto> responseEntity = restTemplate.exchange(requestEntity,
                    new ParameterizedTypeReference<>() {
                    });
            dto = Objects.requireNonNull(responseEntity.getBody()).getResults().get(0);
            System.out.println(dto);
        } catch (Exception e) {
            System.out.println("123");
            throw new HostException("no quest");
        }

        return  questions.put(requestDto.getQuestionId(),
                new Question(requestDto.getQuestionId(), dto.getQuestion(), dto.getCorrectAnswer(), dto.getIncorrectAnswers(), dto.getDifficulty()));
    }
}




