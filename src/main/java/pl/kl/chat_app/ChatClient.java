package pl.kl.chat_app;

import lombok.extern.slf4j.Slf4j;
import pl.kl.chat_app.client.Client;

import java.io.IOException;

@Slf4j
public class ChatClient {

    private static final int PORT = 9999;
    private static final String HOST = "localhost";

    public static void main(String[] args) {
        final Client client;
        try {
            client = new Client(PORT, HOST);
            client.start();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
