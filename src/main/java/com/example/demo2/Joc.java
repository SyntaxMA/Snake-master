package com.example.demo2;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Joc implements Serializable {

    private List<Player> players = new ArrayList<>();

    private List<Manzana> manzanas= new ArrayList<>();

    public Joc() {
    }

    public Joc(List<Player> players) {
        this.players = players;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<Manzana> getManzana() {
        return manzanas;
    }

    public void setManzanas(List<Manzana> bales) {
        this.manzanas = manzanas;
    }
}
