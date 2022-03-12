package com.example.gm.services;


import com.example.gm.dto.LeaderBoard;
import com.example.gm.dto.RequestDto;
import com.example.gm.dto.ResponseDtoAnswer;

public interface GmService {
    ResponseDtoAnswer answerQuestion(RequestDto requestDto);

    LeaderBoard getLeaderBoard(int gameId);

}
