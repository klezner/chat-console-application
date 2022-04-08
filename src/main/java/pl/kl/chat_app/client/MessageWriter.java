package pl.kl.chat_app.client;

import lombok.extern.slf4j.Slf4j;
import pl.kl.chat_app.common.Actions;
import pl.kl.chat_app.common.FileSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

@Slf4j
public class MessageWriter implements Runnable {

    private static final String SPLIT_REGEX = " ";
    private static final int SPLIT_LIMIT = 2;
    private final Socket clientSocket;
    private final BufferedReader consoleReader;
    private final PrintWriter socketWriter;
    private final FileSender fileSender;

    public MessageWriter(Socket clientSocket, BufferedReader consoleReader, PrintWriter socketWriter) {
        this.clientSocket = clientSocket;
        this.consoleReader = consoleReader;
        this.socketWriter = socketWriter;
        this.fileSender = new FileSender(clientSocket);
    }

    @Override
    public void run() {
        while (!clientSocket.isClosed()) {
            try {
                String message;
                while ((message = consoleReader.readLine()) != null) {
                    if (Actions.CLOSE_CONNECTION.getInput().equalsIgnoreCase(message)) {
                        socketWriter.println(message);
                        Thread.sleep(200);
                        close();
                    } else if (message.startsWith(Actions.UPLOAD_FILE.getInput())) {
                        final String filePath = message.split(SPLIT_REGEX, SPLIT_LIMIT)[1];
                        if (fileSender.ifFileExists(filePath)) {
                            socketWriter.println(message);
                            fileSender.send(filePath);
                        } else {
                            System.out.println("File does not exists. Try with another filename");
                        }
                    } else {
                        socketWriter.println(message);
                    }
                }
            } catch (IOException | InterruptedException e) {
                close();
            }
        }
    }

    private void close() {
        try {
            socketWriter.close();
            consoleReader.close();
            fileSender.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
