package com.example.gm.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;


@AllArgsConstructor
@Getter
@EqualsAndHashCode(of = {"name"})
public class Player {
    private final String name;
    private final Map<Integer, Double> results;


    public Player(String name) {
        this.name = name;
        this.results = new HashMap<>();
    }

    public void setPoints(int gameId, double points) {
        results.merge(gameId, points, Double::sum);
    }
}
