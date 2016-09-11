package ru.levelp;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.SocketException;

/**
 * Created by Мария on 10.09.2016.
 */
public class Client {
        private BufferedReader reader;
        private PrintWriter writer;
        private Socket socket;
        private  BufferedReader consoleReader;
        private static final String IP = "127.0.0.1";
        private static final int PORT = 8080;

        public Client () {
        try {
            socket = new Socket(IP, PORT);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            consoleReader = new BufferedReader(new InputStreamReader(System.in));
            ClientMessageHandler clientMessageHandler = new ClientMessageHandler();
            clientMessageHandler.start();

            System.out.println("Введите свой ник(логин) или нажмите \"exit\" для выхода из чата:");
            while(true) {
                String message = consoleReader.readLine();
                if (message == null ) {
                    break;
                }
                writer.println(message);
                writer.flush();
                if (message.equals("exit")) {
                    break;
                }
            }
                clientMessageHandler.setStopHandler();
                reader.close();
                writer.close();
                socket.close();
            } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private class ClientMessageHandler extends Thread{
        private boolean stopped;

        public void setStopHandler (){
            stopped = true;
        }
        @Override
        public void run(){
            try {
            while (!stopped) {
                String str = reader.readLine();
                if (str == null) {
                    break;
                }
                System.out.println(str);
            }
                }  catch (SocketException e){
                    e.printStackTrace();
                    System.out.println("Аварийное завершение работы");
                }   catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Ошибка при подключении");
                }
            }

        }
    }
