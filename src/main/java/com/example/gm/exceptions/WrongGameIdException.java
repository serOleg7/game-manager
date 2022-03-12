package com.example.gm.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class WrongGameIdException extends RuntimeException {
    public WrongGameIdException() {
        super("Wrong request, may be there no such gameId");
    }
}
