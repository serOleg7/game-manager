package com.example.gm.dto;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class RequestDto {
    private String userName;
    private int gameId;
    private int questionId;
    private int answerId;

}
