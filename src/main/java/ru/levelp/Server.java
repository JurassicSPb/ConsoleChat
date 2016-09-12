package ru.levelp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
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
                name = reader.readLine();
                for (ClientHandler c : clients) {
                    c.writer.println(name + " вошел в чат");
                    System.out.println(name + " вошел в чат");
                }
                String str;
                String [] part;
                while (true) {
                    str = reader.readLine();
                    part = str.split("~");
                    String part1 = part[0];
                    if (str.equals("exit")) {break;}
                    for (int i=0; i<clients.size(); i++){
                        if (clients.get(i).name.equals(name)) {
                            continue;
                        }
                        else if ((clients.get(i).name).equals(part1)){
                            clients.get(i).writer.println("Вам пришло личное сообщение от собеседника: ");
                            clients.get(i).writer.println(name + ": " + str);
                            writer.flush();
                        }
                        else if (str.indexOf("~", clients.get(i).name.length()) ==-1){
                            System.out.println(name + ": " + str);
                            clients.get(i).writer.println(name + ": " + str);
                            writer.flush();
                        }
                    }
                }
                for (ClientHandler c : clients) {
                    c.writer.println(name + " покинул чат");
                    System.out.println(name + " покинул чат");
                    writer.flush();
                }
                close();

            } catch (SocketException e){
                e.printStackTrace();
                System.out.println("Аварийное завершение работы");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Ошибка при подключении");
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
