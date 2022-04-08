package pl.kl.chat_app.server;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

@Slf4j
public class Server {

    private final ChatServerFactory factory = new MainChatServerFactory();
    private final ClientHandlers clientHandlers = factory.createClientHandlers();
    private final ExecutorService pool = factory.createCachedThreadPool();
    private final int port;
    private ServerSocket serverSocket;

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            connectClient();
        } catch (Exception e) {
            log.error(e.getMessage());
            closeServer();
        }
    }

    private void connectClient() throws IOException {
        log.info("Server is listening on port: {}", port);
        while (!serverSocket.isClosed()) {
            final Socket clientSocket = serverSocket.accept();
            log.info("New connection established");
            final ClientHandler clientHandler = new ClientHandler(clientSocket, clientHandlers);
            clientHandlers.add(clientHandler);
            pool.execute(clientHandler);
        }
    }

    private void closeServer() {
        try {
            pool.shutdown();
            serverSocket.close();
            for (ClientHandler clientHandler : clientHandlers.getAll()) {
                clientHandler.closeSocket();
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
