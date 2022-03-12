package com.example.gm.model;

import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
public class LeaderBoard {
    private final int gameId;
    private final Map<String, Double> results;

    public LeaderBoard(int gameId) {
        this.gameId = gameId;
        this.results = new HashMap<>();
    }


    //TODO sync lock/check?
    public void updateLeaderBoard(String name, double points) {
        synchronized (name) {
            results.merge(name, points, Double::sum);
        }
    }


}
