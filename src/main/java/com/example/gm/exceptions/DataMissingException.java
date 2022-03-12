package com.example.gm.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class DataMissingException extends RuntimeException {
    public DataMissingException(){
        super("DataMissingException, Name not provided");
    }

}
