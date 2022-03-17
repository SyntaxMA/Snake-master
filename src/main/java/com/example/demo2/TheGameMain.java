package com.example.demo2;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TheGameMain extends Application {

    private double cicles = 0;

    ///////////7

    Client client;

    /////////////777

    float viewPortX = 1200;
    float viewPortY = 800;

    private Pane root = new Pane();
    private int margeJugadors = 25;
    private boolean carrega;
    private final Sprite player1 = new Sprite("player", Color.DARKOLIVEGREEN, 100, 100, 60, 90, Player.Direccio.S, 25);
    private List<Sprite> enemics = new ArrayList<>();

    private List<String> input = new ArrayList<>();
    private int id;
    private long idBales;


    private Parent createContent() {
        root.setPrefSize(viewPortX, viewPortY);
        root.getChildren().add(player1);


        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                update();
                enemics.forEach(e -> root.getChildren().add(e));

            }
        };
        timer.start();

        return root;
    }

    double ciclesMov = cicles;

    private void update() {

        updateEstatJoc();


        input.forEach(s -> {

            /////  PROVANT DIAGONALS


            List<String> dir = new ArrayList<>();


            if (s.equals("D") || s.equals("RIGHT")) {
                player1.setDireccio(Player.Direccio.E);
                if (player1.getTranslateX() + player1.getWidth() + margeJugadors < viewPortX)
                    dir.add("-Dreta");

            }
            if (s.equals("A") || s.equals("LEFT")) {
                player1.setDireccio(Player.Direccio.W);
                if (player1.getTranslateX() - margeJugadors > 0)
                    dir.add("-Esquerre");

            }
            if (s.equals("W") || s.equals("UP")) {
                player1.setDireccio(Player.Direccio.N);
                if (player1.getTranslateY() - (float) margeJugadors * 0.8 > 0)
                    dir.add("-Dalt");


            }
            if (s.equals("S") || s.equals("DOWN")) {

                player1.setDireccio(Player.Direccio.S);
                if (player1.getTranslateY() + player1.getHeight() + margeJugadors < viewPortY)
                    dir.add("-Baix");


            }


            StringBuilder direccioFinal = new StringBuilder();
            dir.forEach(direccioFinal::append);

            switch (direccioFinal.toString()) {
                case "-Dalt-Baix":
                case "-Baix-Dalt":
                case "-Esquerre-Dreta":
                case "Dreta-Esquerre":
                    break;
                case "-Dalt-Dreta":
                case "-Dreta-Dalt":
                    player1.moveRightUp();
                    break;
                case "-Dalt-Esquerre":
                case "-Esquerre-Dalt":
                    player1.moveLeftUp();
                    break;
                case "-Baix-Esquerre":
                case "-Esquerre-Baix":
                    player1.moveLeftDown();
                    break;
                case "-Baix-Dreta":
                case "-Dreta-Baix":
                    player1.moveRightDown();
                    break;
                case "-Baix":
                    player1.moveDown();
                    break;
                case "-Dalt":
                    player1.moveUp();
                    break;
                case "-Esquerre":
                    player1.moveLeft();
                    break;
                case "-Dreta":
                    player1.moveRight();
                    break;
            }
            dir = new ArrayList<>();
        });

        ciclesMov = cicles;
        input = new ArrayList<>();


        sprites().stream()
                .filter(s -> s.getType().equals("atac"))
                .filter(s -> s.getDireccio() == Player.Direccio.S)
                .forEach(Sprite::moveDown);
        sprites().stream()
                .filter(s -> s.getType().equals("atac"))
                .filter(s -> s.getDireccio() == Player.Direccio.N)
                .forEach(Sprite::moveUp);
        sprites().stream()
                .filter(s -> s.getType().equals("atac"))
                .filter(s -> s.getDireccio() == Player.Direccio.W)
                .forEach(Sprite::moveLeft);
        sprites().stream()
                .filter(s -> s.getType().equals("atac"))
                .filter(s -> s.getDireccio() == Player.Direccio.E)
                .forEach(Sprite::moveRight);


        // Aixó per que morin els players
        sprites().stream()
                .filter(sprite -> sprite.getType().equals("atac"))
                .forEach(atac -> {
                    if (atac.getBoundsInParent().intersects(player1.getBoundsInParent())) {
                        player1.setDead(true);

                    }
                });

        // eliminar atacs que han sortit del joc per cada costat de la pantalla :  només els poso a DEAD=true;

        sprites().stream()
                .filter(s -> s.getType().equals("atac"))
                .filter(s -> s.getTranslateY() + s.getHeight() < 0 || s.getTranslateY() - s.getHeight() > viewPortY
                        || s.getTranslateX() + s.getWidth() < 0 || s.getTranslateX() - s.getWidth() > viewPortX)
                .forEach(s -> s.setDead(true));

        // Esborrar els Sprites que estan DEAD=true


        // intentant esborrar les bales que surten de pantalla o xoquen, o... que estan      dead = true;


        sprites().forEach(sprite -> {
            if (sprite.isDead()) {
                root.getChildren().remove(sprite);
            }
        });

        cicles++;
    }

    public Sprite getPlayer1() {
        return player1;
    }

    private void updateEstatJoc() {

        // mirar el joc i per cada enemic, és a dir tots menys el meu player

        if (client == null) {
            client = new Client("localhost", 5555, this);
            client.start();
        }
        // akí posem el ready.

        if (client.isReady()) {
            this.id = client.getIdPlayer();
            idBales=id;
            sprites().forEach(sprite -> {
                if (sprite.getType().equals("enemic")) {
                    root.getChildren().remove(sprite);
                }
            });

            enemics = new ArrayList<>();
            client.getJoc().getPlayers().forEach(p -> {
                if (p.getId() != id) {
                    // actualitzem tots els players menys el nostre   // de moment els tornem a crear
                    enemics.add(new Sprite("enemic", Color.DARKOLIVEGREEN, (int) p.getPosX(), (int) p.getPosY(), 60, 90, p.getDireccio(), 25));

                } else {
             /*   // akí el nostre
                p.setDireccio(player1.getDireccio());
                p.setPosX((int)player1.getTranslateX());
                p.setPosY((int)player1.getTranslateY());*/
                }
                int atacW = 16;
                int atacH = 8;
                int altura = 20;
                for (Manzana b :
                        client.getJoc().getManzana()) {
                    sprites().add(new Sprite("atac", Color.RED, (int) (b.getPosX() - atacW),
                            (int) (b.getPosY() + atacH + altura),
                            atacW, atacH));
                }


            });


        }

    }


    private List<Sprite> sprites() {
        return root.getChildren().stream().map(n -> (Sprite) n).collect(Collectors.toList());
    }


    double ciclesDispar;

    @Override
    public void start(Stage stage) throws Exception {

        Scene scene = new Scene(createContent());

        // posem a escoltar diferents tecles per als inputs

        scene.setOnKeyPressed(e -> {
            input.add(e.getCode().toString());
        });


        stage.setScene(scene);
        stage.show();


    }

    public static void main(String[] args) {


        launch();

    }


}
