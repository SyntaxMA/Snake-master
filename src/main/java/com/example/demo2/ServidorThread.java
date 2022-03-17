package com.example.demo2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ServidorThread extends Thread {

    JsonClass json;
    Socket clientSocket = null;
    BufferedReader in = null;
    PrintStream out = null;
    String msgEntrant, msgSortint;
    boolean acabat;
    Joc estatJoc;
    int idPropia;
    String nick;

    public ServidorThread(Socket clientSocket, Joc estatJoc, int idPropia) {
        this.idPropia = idPropia;
        this.estatJoc = estatJoc;
        this.clientSocket = clientSocket;
        acabat = false;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintStream(clientSocket.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
        json = new JsonClass();

    }

    @Override
    public void run() {
        System.out.println("i. jug_" + (idPropia) + " Conexió establerta");
        try {
            // primer missatge on li passem el num de player per determinar la posició inicial i el color del usuari
            estatJoc.getPlayers().add(new Player(idPropia, 100f, 100f + (idPropia * 100), Player.Direccio.S));
            msgSortint = String.valueOf(idPropia);
            out.println(msgSortint);
            out.flush();
            System.out.println("o. jug_" + (idPropia) + ": " + msgSortint);
            //rebem la confirmació del client i
            // enviem el primer json amb tots els players, l'usuari ja sap la seva id així que ja podrà discernir que ha de actualitzar i que no.
            msgEntrant = in.readLine();
            System.out.println("i. jug_" + (idPropia) + ": " + msgEntrant);
            if (msgEntrant.equals("Conectat!")) {
                // akí podriem rebre el nick del jugador per a que tots veiessin el nom dels altres ara tiro milles
                // TODO posar nick al client per a que el servidor tingui el nom dels clients i poder-los mostrar en un futur
            }

            msgSortint = json.getJSON(estatJoc);
            out.println(msgSortint);
            out.flush();


            // Akí hem de rebre el primer json del client

            msgEntrant = in.readLine();

            // akí comença la festa dels json
            while (!acabat) {
                msgSortint = generarResposta(msgEntrant);
                out.println(msgSortint);
                out.flush();
                msgEntrant = in.readLine();
                // bucle de missatges
            }
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
            System.out.println("Salta akí ");
        }
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generarResposta(String msgEntrant) {

        JsonClass json = new JsonClass();
        Joc jocRebut = json.getObject(msgEntrant);

        estatJoc.getPlayers().forEach(player -> {
            if (player.getId() == idPropia) {
                for (Player p :
                        jocRebut.getPlayers()) {
                    if (p.getId() == idPropia) {
                        player.setPosX(p.getPosX());
                        player.setPosY(p.getPosY());
                        player.setDireccio(p.getDireccio());
                        player.setId(p.getId());

                    }
                }

            }
        });

        boolean existeix = false;
        // Per cada bala rebuda
        for (Manzana bReb : jocRebut.getManzana()) {
            // per cada bala que existeix acyualment
            for (Manzana bAct :
                    estatJoc.getManzana()) {
            }
            // si no existeix la creem, sino l'actualitzem la posició i dir
            if (!existeix) estatJoc.getManzana().add(bReb);
            else {
                estatJoc.getManzana().forEach(b -> {

                });
            }
            existeix = false;
        }
        String resposta = json.getJSON(estatJoc);

        // per monitoritzar el que passa al servidor
        System.out.println("i. jug_" + (idPropia + 1) + ": " + msgEntrant);
        System.out.println("o. jug_" + (idPropia + 1) + ": " + resposta);

        return resposta;//new Scanner(System.in).nextLine();
    }
}
