package com.example.gm.services;

import com.example.gm.dto.*;
import com.example.gm.enums.AnswerStatus;
import com.example.gm.exceptions.*;
import com.example.gm.model.LeaderBoard;
import com.example.gm.model.Player;
import com.example.gm.model.Question;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GmServiceImpl implements GmService {
    private static final String OPENT_DB = "https://opentdb.com/api.php";
    private final Map<String, Question> questions = new HashMap<>();
    private final Map<String, Player> players = new HashMap<>();
    private final Map<Integer, LeaderBoard> leaderBoards = new HashMap<>();


    @Override
    public ResponseDtoAnswer answerQuestion(RequestDto requestDto) {
        checkExceptionsStatus(requestDto);
        if (!players.containsKey(requestDto.getUserName()))
            registerNewPlayer(requestDto.getUserName());

        Question question = questions.getOrDefault(getUniqueId(requestDto), getQuestionFromApi(requestDto));
        LeaderBoard lb = getOrCreateLeaderBoard(requestDto.getGameId());
        ResponseDtoAnswer responseDtoAnswer = new ResponseDtoAnswer();
        if (requestDto.getAnswerId() == question.getIntCorrectAnswer()) {
            responseDtoAnswer.setAnswerStatus(AnswerStatus.RIGHT_ANSWER);
            responseDtoAnswer.setPointsEarned(question.getPoints());
            lb.updateLeaderBoard(requestDto.getUserName(), question.getPoints());

        } else
            lb.updateLeaderBoard(requestDto.getUserName(), 0);

        question.addPlayer(requestDto.getUserName());
//        System.out.println("qu: " + questions);
//        System.out.println("le: " + leaderBoards);
        return responseDtoAnswer;
    }

    private void checkExceptionsStatus(RequestDto requestDto) {
        if (requestDto.getUserName() == null)
            throw new DataMissingException();
        if (!checkIfPlayerCanPlay(requestDto))
            throw new PlayerPermissionDeniedException(requestDto.getUserName());
    }


    private boolean checkIfPlayerCanPlay(RequestDto requestDto) {
        if (!questions.containsKey(getUniqueId(requestDto)))
            return true;
        return !questions.get(getUniqueId(requestDto)).getPlayersWhichAnswered().contains(requestDto.getUserName());
    }

    private void registerNewPlayer(String userName) {
        players.put(userName, new Player(userName));
    }

    private LeaderBoard getOrCreateLeaderBoard(int gameId) {
        LeaderBoard lb = leaderBoards.get(gameId);
        if (lb == null) {
            lb = new LeaderBoard(gameId);
            leaderBoards.put(gameId, lb);
        }
        return lb;
    }

    @Override
    public Map<String, Double> getLeaderBoard(int gameId) {
        if (leaderBoards.containsKey(gameId)) {
            return leaderBoards.get(gameId).getResults().entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        } else
            throw new BadRequestException(gameId);
    }

    private String getUniqueId(RequestDto requestDto) {
        return requestDto.getGameId() + "|" + requestDto.getQuestionId();

    }


    private Question getQuestionFromApi(RequestDto requestDto) {
        synchronized (getUniqueId(requestDto)) {
            if (questions.containsKey(getUniqueId(requestDto)))
                return questions.get(getUniqueId(requestDto));
            //TODO add check if question unique / add Set<String>
            ResponseDto body;
            try {
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(OPENT_DB)
                        .queryParam("amount", 1)
                        .queryParam("category", requestDto.getGameId());
                RequestEntity<String> requestEntity = new RequestEntity<>(HttpMethod.GET, builder.build().toUri());
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<ResponseDto> responseEntity = restTemplate.exchange(requestEntity,
                        new ParameterizedTypeReference<>() {
                        });
                body = responseEntity.getBody();
            } catch (RuntimeException e) {
                throw new HostNotReachableException();
            }
            QuestionDto dto = Objects.requireNonNull(body).getResults().stream().findFirst().orElseThrow(WrongGameIdException::new);
            return questions.put(getUniqueId(requestDto),
                    new Question(requestDto.getGameId(), requestDto.getQuestionId(), dto.getQuestion(), dto.getCorrectAnswer(), dto.getIncorrectAnswers(), dto.getDifficulty()));
        }

    }

}





