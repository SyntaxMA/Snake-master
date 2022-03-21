package com.example.demo2;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.File;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;

public class Main extends Application implements Serializable{

    // Tamaño de los cuadrados, hay que tener en cuenta que si un cubo mide 30 y el mapa es 20 x 20 será una pantalla de 600 x 600
    public static int block_size = 30;

    GraphicsContext graphics_context;
    private ObjectInputStream objectInputStream = null;
    private ObjectOutputStream objectOutputStream = null;
    private boolean isServer;

    // Preparamos sockets y a los dos jugadoress que se van a enfrentar entre sí y la manzana.
    Socket socket;
    private Game_Info game_info;
    private Snake player1;
    private Snake player2;
    int vic_player1;
    int vic_player2;

    String path = "src/main/resources/manzana_sound.wav";
    Media media = new Media(new File(path).toURI().toString());
    MediaPlayer mediaPlayer = new MediaPlayer(media);

    Timer timer = new Timer();

    private AppleAndBody apple;

    private boolean game_over = false;

    private Label label;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        primaryStage.setTitle("Snake");
        label = new Label("");

        player1 = new Snake(30, 30, "RIGHT");
        player2 = new Snake(20 * block_size - 60, 20 * block_size - 60, "LEFT");

        apple = new AppleAndBody(0, 0);
        apple.randomPos();

        game_info = new Game_Info(player1, player2, apple);

        //Tamaño del mapa donde se va jugar
        Canvas canvas = new Canvas();
        canvas.setHeight(20 * block_size);
        canvas.setWidth(20 * block_size);
        graphics_context = canvas.getGraphicsContext2D();
        drawMap();

        // Creamos la conexion
        try
        {
            //Abrirá el socket para realizar la conexión
            socket = new Socket(InetAddress.getLocalHost(), 5555);
            System.out.println("Conectando al host...");
            isServer = false;
        }

        catch (Exception e)
        {
            //Si no hay una sala creada, haces de server...
            ServerSocket server = new ServerSocket(5555);
            System.out.println("Haciendo de host...");
            System.out.println("Esperando a otro jugador...");
            socket = server.accept();
            System.out.println("Se ha unido un jugador");
            isServer = true;
        }

        // Envío de información
        OutputStream outputStream = socket.getOutputStream();
        objectOutputStream = new ObjectOutputStream(outputStream);

        InputStream inputStream = socket.getInputStream();
        objectInputStream = new ObjectInputStream(inputStream);
        if(isServer)
        {
            try {
                objectOutputStream.writeObject(game_info);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Recibe la informacion
        else
        {
            try {
                game_info = (Game_Info) objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            player1 = game_info.player1;
            player2 = game_info.player2;
            apple = game_info.apple;
        }

        // Tiempo en bucle
        new AnimationTimer()
        {
            int frame = 1;

            public void handle(long currentNanoTime)
            {
                if(frame == 30)
                {
                    //El jugador 2 se comunica con el jugador 1 que hace de host
                    if(isServer)
                    {
                        try {
                            player2.direction = (String) objectInputStream.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }

                        //Movimientos de serpiente y sus segmentos
                        player1.move();
                        player2.move();

                        //Saber si la cabeza de la serpiente del player 1 toca con una manzana
                        if(player1.body.getFirst().x == apple.x && player1.body.getFirst().y == apple.y)
                        {
                            mediaPlayer.setAutoPlay(true);
                            player1.createSegment();
                            apple.randomPos();
                            mediaPlayer.play();
                            mediaPlayer.stop();

                        }

                        //Saber si la cabeza de la serpiente del player 2 toca con una manzana
                        if(player2.body.getFirst().x == apple.x && player2.body.getFirst().y == apple.y)
                        {
                            mediaPlayer.setAutoPlay(true);
                            player2.createSegment();
                            apple.randomPos();
                            mediaPlayer.play();
                            mediaPlayer.stop();
                        }

                        //Comprobar colisiones
                        boolean player1_colliding;
                        player1_colliding = player1.checkColision();
                        if(!player1_colliding) player1_colliding = player1.checkColision(player2);

                        boolean player2_colliding;
                        player2_colliding = player2.checkColision();
                        if(!player2_colliding) player2_colliding = player2.checkColision(player1);


                        //Si los dos chocan entre ellos empate
                        if(player1_colliding && player2_colliding)
                        {
                            game_over = true;
                            player1.direction = "DRAW";
                            player2.direction = "DRAW";
                        }
                        //Si el jugador 1 choca gana player 2
                        else if(player1_colliding)
                        {
                            game_over = true;
                            player1.direction = "P2";
                            player2.direction = "P2";
                        }

                        //Si el jugador 2 choca gana player 1
                        else if(player2_colliding)
                        {
                            game_over = true;
                            player1.direction = "P1";
                            player2.direction = "P1";
                        }

                        game_info.player1 = player1;
                        game_info.player2 = player2;
                        game_info.apple = apple;

                        //Actualizar informacion al host
                        try {
                            objectOutputStream.reset();
                            objectOutputStream.writeObject(game_info);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    else
                    {
                        //Actualizar informacion al jugador 2
                        try {
                            objectOutputStream.reset();
                            objectOutputStream.writeObject(player2.direction);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //Comprobar en cualquier momento la informacion de los jugadores y donde hay una manzana.
                        try {
                            game_info = (Game_Info) objectInputStream.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }

                        player1 = game_info.player1;
                        player2 = game_info.player2;
                        apple = game_info.apple;
                    }

                    if(player1.direction.equals("DRAW") || player2.direction.equals("DRAW"))
                    {
                        label.setText("¡EMPATE!");
                        this.stop();
                    }

                    else if(player1.direction.equals("P1") || player2.direction.equals("P1"))
                    {
                        label.setText("¡JUGADOR 1 HA GANADO LA PARTIDA!");
                        this.stop();
                    }

                    else if(player1.direction.equals("P2") || player2.direction.equals("P2"))
                    {
                        label.setText("¡JUGADOR 2  HA GANADO LA PARTIDA!");
                        this.stop();
                    }

                    // Dibuja todos los elementos del juego
                    drawMap();
                    drawCabezaSnake(player1);
                    drawCuerpoSnake(player1);
                    drawCabezaSnake(player2);
                    drawCuerpoSnake(player2);
                    drawApple();
                }

                frame++;
                if(frame == 31) frame = 1;
            }
        }.start();

        Group group = new Group(canvas);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(group);
        stackPane.getChildren().add(label);
        Scene scene = new Scene(stackPane, block_size * 20, block_size * 20);

        //Movimiento de las serpientes
        
        scene.setOnKeyPressed(
                keyEvent -> {
                    if(!game_over)
                    {
                        if(keyEvent.getCode().toString().equals("UP"))
                        {
                            if(isServer) player1.direction = "UP";
                            else player2.direction = "UP";
                        }

                        if(keyEvent.getCode().toString().equals("DOWN"))
                        {
                            if(isServer) player1.direction = "DOWN";
                            else player2.direction = "DOWN";
                        }

                        if(keyEvent.getCode().toString().equals("RIGHT"))
                        {
                            if(isServer) player1.direction = "RIGHT";
                            else player2.direction = "RIGHT";
                        }

                        if(keyEvent.getCode().toString().equals("LEFT"))
                        {
                            if(isServer) player1.direction = "LEFT";
                            else player2.direction = "LEFT";
                        }
                    }
                }
        );

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Dibujar el tablero del juego
    public void drawMap()
    {
        int r = 0;
        for(int i = 0; i < 20 * block_size; i += block_size)
        {
            for(int j = 0; j < 20 * block_size; j += block_size)
            {
                if(r % 2 == 0) graphics_context.setFill(Color.WHITESMOKE);
                else graphics_context.setFill(Color.GHOSTWHITE);
                graphics_context.fillRect(j, i, block_size, block_size);
                r++;
            }
            r++;
        }
    }

    // Dibujar las cabezas de serpientes con sus colores con un rectangulo
    public void drawCabezaSnake(Snake player)
    {
        {
            if(player == player1) graphics_context.setFill(Color.GREEN);
            else graphics_context.setFill(Color.MAGENTA);
            graphics_context.fillRect(player.body.getFirst().x, player.body.getFirst().y, block_size, block_size);
        }
    }

    // Dibujar el cuerpo de las serpientes con sus colores con un ovalo
    public void drawCuerpoSnake(Snake player)
    {
        for(int i = 1; i < player.body.size(); i++)
        {
            if(player == player1) graphics_context.setFill(Color.DARKGREEN);
            else graphics_context.setFill(Color.DARKMAGENTA);
            graphics_context.fillOval(player.body.get(i).x, player.body.get(i).y, block_size, block_size);
        }
    }

    // Dibujar la manzana
    public void drawApple()
    {
        graphics_context.setFill(Color.RED);
        graphics_context.fillOval(apple.x, apple.y, block_size, block_size);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop(){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
