package com.example.demo2;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Client extends Thread {

    String hostname;
    int port;
    JsonClass json;
    boolean continueConnected;

    private Joc joc;
    private Joc coj;


    private int idPlayer;
    private boolean ready;
    TheGameMain gameMain;


    public Client(String hostname, int port,TheGameMain gameMain) {
        this.hostname = hostname;
        this.port = port;
        continueConnected = true;
        List<Player> playersJoc=new ArrayList<>();
        List<Player> playersCoj= new ArrayList<>();

        this.gameMain=gameMain;
        joc = new Joc( playersJoc);
        coj = new Joc(playersCoj);

        json=new JsonClass();
    }


    public void run() {

        String serverData;
        String request;


        Socket socket;
        BufferedReader in;
        PrintStream out;
        try {
            socket = new Socket(InetAddress.getByName(hostname), port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream());

            // Tractem el primer missatge on rebem la id
            serverData = in.readLine();
            System.out.println("i. " + serverData);
            this.idPlayer = Integer.parseInt(serverData);
            // ~enviem el nick~  enviem resposta simple de moment
            // TODO enviar el nick que hauriem d'haver demanat
            request = "Conectat!";
            ready=true;

            out.println(request);
            out.flush();
            System.out.println("o. " + request);

            // revem segon missatge amb el json ja. toca iniciar tot:
            // - posar el jugador propi al seu lloc nou
            serverData = in.readLine();
            System.out.println("i. " + serverData);

            joc=json.getObject(serverData);

            ready=true;
            // comença la festa dels Json
            out.println(json.getJSON(joc));
            out.flush();
            while (continueConnected) {
                serverData = in.readLine();

                //processament de les dades rebudes i obtenció d'una nova petició
                request = getRequest(serverData);
                //enviament el número i els intents
                out.println(request);
                System.out.println("o. " + request);
                out.flush();
            }
            close(socket);
        } catch (UnknownHostException ex) {
            System.out.println("Error de connexió. No existeix el host: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("Error de connexió indefinit: " + ex.getMessage());
        }

    }

    public Joc getJoc() {
        return joc;
    }

// Tractem la rebuda de dades

    public String getRequest(String recivedDataFromServer) {

        JsonClass json = new JsonClass();
        // ens pillem tota la info.            després només actualitzarem als altres players en el joc. pero abans de retornar el json hem d'actualitzar el player
        joc=json.getObject(recivedDataFromServer);
        //joc.actualitzaClient(idPlayer, recivedDataFromServer);

       // try{}catch(Exception e){e.printStackTrace();}
        joc.getPlayers().stream().filter(p-> p.getId()==idPlayer).toList().get(0).setPosY((int)(gameMain.getPlayer1().getTranslateY()));
        joc.getPlayers().stream().filter(p-> p.getId()==idPlayer).toList().get(0).setPosX((int)(gameMain.getPlayer1().getTranslateX()));
        joc.getPlayers().stream().filter(p-> p.getId()==idPlayer).toList().get(0).setDireccio((gameMain.getPlayer1().getDireccio()));



        String resposta = json.getJSON(joc);


        // monitoritzar el que rebem del servidor
        System.out.println("i.  " + recivedDataFromServer);


        return resposta;


    }

    public void setJoc(Joc joc) {
        this.joc = joc;
    }

    public Joc getCoj() {
        return coj;
    }

    public void setCoj(Joc coj) {
        this.coj = coj;
    }

    public void setIdPlayer(int idPlayer) {
        this.idPlayer = idPlayer;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean mustFinish(String dades) {
        if (dades.equals("exit")) return false;
        return true;

    }

    private void close(Socket socket) {
        //si falla el tancament no podem fer gaire cosa, només enregistrar
        //el problema
        try {
            //tancament de tots els recursos
            if (socket != null && !socket.isClosed()) {
                if (!socket.isInputShutdown()) {
                    socket.shutdownInput();
                }
                if (!socket.isOutputShutdown()) {
                    socket.shutdownOutput();
                }
                socket.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public boolean isReady() {
        return ready;
    }

    public int getIdPlayer() {
        return idPlayer;
    }
}
