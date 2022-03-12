package com.example.gm.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class HostNotReachableException extends RuntimeException {
    public HostNotReachableException() {
        super("Server not found");
    }
}
