package com.example.gm.controller;

import com.example.gm.dto.RequestDto;
import com.example.gm.dto.ResponseDtoAnswer;
import com.example.gm.services.GmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/game")
public class GmController {
    final GmService gmService;

    @Autowired
    public GmController(GmService gmService) {
        this.gmService = gmService;
    }

    @PostMapping
    public ResponseDtoAnswer checkQuestion(@RequestBody RequestDto requestDto) {
        return gmService.answerQuestion(requestDto);
    }

    @GetMapping("/leaderboard/{gameId}")
    public Map<String, Double> getLeaderBoard(@PathVariable Integer gameId){
        return gmService.getLeaderBoard(gameId);
    }
}
