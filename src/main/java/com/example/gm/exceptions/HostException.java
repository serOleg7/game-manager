package com.example.gm.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class HostException extends RuntimeException {
    public HostException(String no_quest) {
        super(no_quest + " probloem host");
    }
}
