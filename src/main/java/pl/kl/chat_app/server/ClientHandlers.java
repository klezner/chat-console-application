package pl.kl.chat_app.server;

import java.util.List;

public interface ClientHandlers {

    void add(ClientHandler clientHandler);

    void remove(ClientHandler clientHandler);

    List<ClientHandler> getAll();

    void broadcastMessage(String message, String activeChannel);

    void cacheMessage(String username, String messageFormat, String activeChannel);

    String getAllUsernames();

    String getAllChannelUsernames(String activeChannel);

    String getAllChannelNames();

    boolean isUsernameUsed(String inputUsername);
}
