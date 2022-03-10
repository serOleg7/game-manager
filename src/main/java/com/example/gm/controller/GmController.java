package com.example.gm.controller;

import com.example.gm.dto.RequestDto;
import com.example.gm.dto.ResponseDtoAnswer;
import com.example.gm.services.GmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GmController {
    GmService gmService;

    @Autowired
    public GmController(GmService gmService) {
        this.gmService = gmService;
    }

    @GetMapping("/game")
    public ResponseDtoAnswer checkQuestion(@RequestBody RequestDto requestDto){
        return gmService.getResponse(requestDto);
    }
}
