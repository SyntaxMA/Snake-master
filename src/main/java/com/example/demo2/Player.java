package com.example.demo2;

public class Player {

    private float posY, posX;
    private int id;

    public int getId() {
        return id;
    }

    public Player() {

    }
    /*private BalaEvent bala;*/




  /*  public BalaEvent getBala() {
        return bala;
    }

    public void setBala(BalaEvent bala) {
        this.bala = bala;
    }*/
    private Direccio direccio;


    public Player(int id, float posY, float posX, Direccio direccio) {
        this.posY = posY;
        this.posX = posX;
        this.direccio = direccio;
        this.id=id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public void setPosY(float posY) {
        this.posY = posY;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public void setDireccio(Direccio direccio) {
        this.direccio = direccio;
    }
    public enum Direccio {
        N, S, E, W;

    }
   /* public void dispara(long id,Direccio dir, float x, float y) {  // Els id, aniran de 10 en 10 i sumarem 1 o 0 depenent del player així, si acaba en 1 pertany al segon player, else ...
        id=id+Integer.parseInt(this.id);
        bala = new BalaEvent(id,dir, x, y);
    }

    public void eliminarBala() {
        bala = null;
    }

*/

    public float getPosY() {
        return posY;
    }

    public float getPosX() {
        return posX;
    }

    public Direccio getDireccio() {
        return direccio;
    }




}
