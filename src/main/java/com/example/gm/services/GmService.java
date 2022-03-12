package com.example.gm.services;


import com.example.gm.dto.RequestDto;
import com.example.gm.dto.ResponseDtoAnswer;

import java.util.Map;

public interface GmService {
    ResponseDtoAnswer answerQuestion(RequestDto requestDto);

    Map<String, Double> getLeaderBoard(int gameId);

}
