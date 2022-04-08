package pl.kl.chat_app;

import pl.kl.chat_app.server.Server;

public class ChatServer {

    private static final int PORT = 9999;

    public static void main(String[] args) {
        final Server server = new Server(PORT);
        server.start();
    }

}
