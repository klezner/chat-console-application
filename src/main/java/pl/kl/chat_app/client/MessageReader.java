package pl.kl.chat_app.client;

import lombok.extern.slf4j.Slf4j;
import pl.kl.chat_app.common.Actions;
import pl.kl.chat_app.common.FileReceiver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

@Slf4j
public class MessageReader implements Runnable {

    private final Socket clientSocket;
    private final BufferedReader socketReader;
    private final PrintWriter printer;
    private final FileReceiver fileReceiver;

    public MessageReader(Socket clientSocket, BufferedReader socketReader, PrintWriter printer) {
        this.clientSocket = clientSocket;
        this.socketReader = socketReader;
        this.printer = printer;
        this.fileReceiver = new FileReceiver(clientSocket);
    }

    @Override
    public void run() {
        String message;
        while (!clientSocket.isClosed()) {
            try {
                while ((message = socketReader.readLine()) != null) {
                    if (Actions.CLOSE_CONNECTION.getInput().equalsIgnoreCase(message)) {
                        printer.println(message);
                        Thread.sleep(200);
                        close();
                    } else if (message.startsWith(Actions.DOWNLOAD_FILE.getInput())) {
                        String clientPath = ".\\ClientDownloads\\";
                        fileReceiver.receive(clientPath);
                    } else {
                        printer.println(message);
                    }
                }
            } catch (IOException | InterruptedException e) {
                printer.println("Server -> Connection with server lost");
                close();
            }
        }
    }

    private void close() {
        try {
            socketReader.close();
            printer.close();
            fileReceiver.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
