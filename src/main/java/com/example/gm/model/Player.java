package com.example.gm.model;

import lombok.*;

import javax.persistence.Id;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(of = {"name"})
public class Player {
    @Id
    private String name;
    private Map<Integer, Double> results;


    public Player(String name) {
        this.name = name;
        this.results = new HashMap<>();
    }

    public void setPoints(int gameId, double points) {
        results.merge(gameId, points, Double::sum);
    }
}
