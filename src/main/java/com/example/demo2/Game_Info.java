package com.example.demo2;

import java.io.Serializable;

public class Game_Info implements Serializable
{
    public Snake player1;
    public Snake player2;
    public AppleAndBody apple;

    public Game_Info(Snake player1, Snake player2, AppleAndBody apple)
    {
        this.player1 = player1;
        this.player2 = player2;
        this.apple = apple;
    }
}
