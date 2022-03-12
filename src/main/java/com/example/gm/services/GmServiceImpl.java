package com.example.gm.services;

import com.example.gm.dto.*;
import com.example.gm.enums.AnswerStatus;
import com.example.gm.exceptions.BadRequestException;
import com.example.gm.exceptions.HostNotReachableException;
import com.example.gm.exceptions.PlayerPermissionDeniedException;
import com.example.gm.model.Player;
import com.example.gm.model.Question;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class GmServiceImpl implements GmService {
    private static final String OPENT_DB = "https://opentdb.com/api.php";
    private final Map<String, Question> questions = new HashMap<>();
    private final Map<String, Player> players = new HashMap<>();
    private final Map<Integer, Game> leaderBoard = new HashMap<>();


    @Override
    public ResponseDtoAnswer answerQuestion(RequestDto requestDto) {
        if (!players.containsKey(requestDto.getUserName()))
            registerNewPlayer(requestDto.getUserName());
        if (!checkIfPlayerCanPlay(requestDto))
            throw new PlayerPermissionDeniedException(requestDto.getUserName());

        Question question = questions.getOrDefault(generateUniqueId(requestDto), getQuestionFromApi(requestDto));
        ResponseDtoAnswer responseDtoAnswer = new ResponseDtoAnswer();
        if (requestDto.getAnswerId() == question.getIntCorrectAnswer()) {
            responseDtoAnswer.setAnswerStatus(AnswerStatus.RIGHT_ANSWER);
            responseDtoAnswer.setPointsEarned(question.getPoints());
            players.get(requestDto.getUserName()).setPoints(requestDto.getGameId(), question.getPoints());

        } else
            players.get(requestDto.getUserName()).setPoints(requestDto.getGameId(), 0);

        question.addPlayer(requestDto.getUserName());
        System.out.println("qu: " + questions);

        return responseDtoAnswer;
    }

    @Override
    public LeaderBoard getLeaderBoard(int gameId) {
        //TODO
        return null;
    }

    private String generateUniqueId(RequestDto requestDto) {
        return requestDto.getGameId() + "|" + requestDto.getAnswerId();

    }

    private void registerNewPlayer(String userName) {
        players.put(userName, new Player(userName));
    }

    private boolean checkIfPlayerCanPlay(RequestDto requestDto) {
        //TODO
        return true;
    }


    private Question getQuestionFromApi(RequestDto requestDto) {
        synchronized (generateUniqueId(requestDto)) {
            if (questions.containsKey(generateUniqueId(requestDto)))
                return questions.get(generateUniqueId(requestDto));
            RestTemplate restTemplate = new RestTemplate();
            ResponseDto body;
            try {
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(OPENT_DB)
                        .queryParam("amount", 1)
                        .queryParam("category", requestDto.getGameId());
                RequestEntity<String> requestEntity = new RequestEntity<>(HttpMethod.GET, builder.build().toUri());
                ResponseEntity<ResponseDto> responseEntity = restTemplate.exchange(requestEntity,
                        new ParameterizedTypeReference<>() {
                        });
                body = responseEntity.getBody();
            } catch (RuntimeException e) {
                throw new HostNotReachableException();
            }
            QuestionDto dto = Objects.requireNonNull(body).getResults().stream().findFirst().orElseThrow(BadRequestException::new);
            return questions.put(generateUniqueId(requestDto),
                    new Question(requestDto.getQuestionId(), dto.getQuestion(), dto.getCorrectAnswer(), dto.getIncorrectAnswers(), dto.getDifficulty()));
        }

    }

}





