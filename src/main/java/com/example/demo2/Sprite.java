package com.example.demo2;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Sprite extends Rectangle {


    private int velMoviment= 2;
    int altura=20;

    public Sprite(String type, Color color, int x, int y, int w, int h) {
        super(w,h,color);
        // per carregar els missatges
        this.type=type;
        setTranslateX(x);
        setTranslateY(y);

    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public boolean isDead() {
        return dead;
    }

    boolean dead = false;
    private String type=null;
    Player.Direccio direccio = Player.Direccio.S;

    public Sprite(String type, Color color, int x, int y, int w, int h, Player.Direccio direccio) {
        super(w, h, color);
        this.direccio = direccio;
        this.type = type;
        setTranslateX(x);
        setTranslateY(y);
    }

    // Constructor per especificar la velocitat de moviment
public Sprite(String type, Color color, int x, int y, int w, int h, Player.Direccio direccio, int velocitat) {
        super(w, h, color);
        this.direccio = direccio;
        this.type = type;
        this.velMoviment=velocitat;
        setTranslateX(x);
        setTranslateY(y);
    }

    public void moveRight() {
        setTranslateX(getTranslateX() + velMoviment);
    }

    public void moveLeft() {
        setTranslateX(getTranslateX() - velMoviment);
    }

    public void moveUp() {
        setTranslateY(getTranslateY() - velMoviment);
    }

    public void moveDown() {
        setTranslateY(getTranslateY() + velMoviment);
    }


    public void moveLeftUp() {
        setTranslateY(getTranslateY() - velMoviment);
        setTranslateX(getTranslateX() - velMoviment);
    }
    public void moveLeftDown() {
        setTranslateY(getTranslateY() + velMoviment);
        setTranslateX(getTranslateX() - velMoviment);    }
    public void moveRightUp() {
        setTranslateY(getTranslateY() - velMoviment);
        setTranslateX(getTranslateX() + velMoviment);    }
    public void moveRightDown() {
        setTranslateY(getTranslateY() + velMoviment);
        setTranslateX(getTranslateX() + velMoviment);    }



    public Sprite atacar(Sprite sprite) {



        Sprite s = null;
        int atacW = 16;
        int atacH = 8;


        // Comprovem la posicio de qui fa l'atac i referent a aixo fem que l'atac surti en la direccio en que mira qui fa l'atac
        switch (sprite.getDireccio()) {
            case W -> {
                s = new Sprite("atac", Color.RED, (int) (sprite.getTranslateX()-atacW),
                        (int) (sprite.getTranslateY()+atacH+altura),
                        atacW, atacH, sprite.getDireccio());
            }
            case E -> {
                s = new Sprite("atac", Color.RED, (int) (sprite.getTranslateX() + sprite.getWidth()),
                        (int) (sprite.getTranslateY() +atacH+altura), atacW, atacH, sprite.getDireccio());
            }
            case S -> {
                s = new Sprite("atac", Color.RED, (int) (sprite.getTranslateX()+ sprite.getWidth()/2 -atacH/2),
                        (int) (sprite.getTranslateY()+ sprite.getWidth()-atacW+altura), atacH, atacW, sprite.getDireccio());
            }
            case N -> {
                s = new Sprite("atac", Color.RED, (int) (sprite.getTranslateX()+ sprite.getWidth()/2 -atacH/2),
                        (int) (sprite.getTranslateY()-atacW+altura), atacH, atacW, sprite.getDireccio());
            }

        }
        return s;
    }

    public Player.Direccio getDireccio() {
        return direccio;
    }

    public void setDireccio(Player.Direccio direccio) {
        this.direccio = direccio;
    }

    public String getType() {
        return type;
    }

    public void carregar(Sprite sprite) {
        /*Sprite s= new Sprite("message", Color.TRANSPARENT, (int) (sprite.getTranslateX()),
                (int) (sprite.getTranslateY()),
                120, 50);
        return s;*/
    }
}
