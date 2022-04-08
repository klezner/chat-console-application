package pl.kl.chat_app.client;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Slf4j
public class Client {

    private final Socket clientSocket;
    private final BufferedReader consoleReader;
    private final BufferedReader socketReader;
    private final PrintWriter socketWriter;
    private PrintWriter printer;

    public Client(int port, String host) throws IOException {
        this.printer = new PrintWriter(System.out, true);
        this.clientSocket = new Socket(host, port);
        this.consoleReader = new BufferedReader(new InputStreamReader(System.in));
        this.socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.socketWriter = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    public void start() {
        new Thread(new MessageReader(clientSocket, socketReader, printer)).start();
        new Thread(new MessageWriter(clientSocket, consoleReader, socketWriter)).start();
    }

}
