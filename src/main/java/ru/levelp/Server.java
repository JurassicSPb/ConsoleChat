package ru.levelp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


/**
 * Created by Мария on 10.09.2016.
 */
public class Server {
    private static final int PORT = 8080;
    private ArrayList <ClientHandler> clients = new ArrayList<ClientHandler>();
    private ServerSocket serverSocket;

    public Server() {
        try {
            serverSocket = new ServerSocket(PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket);
                clients.add(handler);
                handler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public class ClientHandler extends Thread {
        private BufferedReader reader;
        private PrintWriter writer;
        private Socket socket;
        private String name = "";

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
                close();
            }
        }
        public void run(){
            try {
                //System.out.println("readline");
                name = reader.readLine();
                //System.out.println("afterread");
                for(ClientHandler c : clients) {
                    c.writer.println(name + " вошел в чат");
                    System.out.println(name + " вошел в чат");
                }
                String str;
                while (true) {
                    str = reader.readLine();
                    if(str.equals("exit")) break;
                    for(ClientHandler c : clients) {
                        System.out.println(name + ": " + str);
                        c.writer.println(name + ": " + str);
                    }
                }
                for(ClientHandler c : clients) {
                    c.writer.println(name + " покинул чат");
                    System.out.println(name + " покинул чат");
                }
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        public void close() {
            try {
                reader.close();
                writer.close();
                socket.close();
                clients.remove(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
