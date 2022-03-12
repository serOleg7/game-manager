package com.example.gm.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class PlayerPermissionDeniedException extends RuntimeException {

    public PlayerPermissionDeniedException(String name){
        super("Player: " + name + " can't answer this question, probably, you have already answered it");
    }

}
