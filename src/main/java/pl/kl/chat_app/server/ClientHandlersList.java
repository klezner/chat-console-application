package pl.kl.chat_app.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ClientHandlersList implements ClientHandlers {

    private static final String DEFAULT_USERNAME = "unknown";
    private final List<ClientHandler> clientHandlers;

    public ClientHandlersList() {
        this.clientHandlers = new ArrayList<>();
    }

    @Override
    public void add(ClientHandler clientHandler) {
        clientHandlers.add(clientHandler);
    }

    @Override
    public void remove(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
    }

    public List<ClientHandler> getAll() {
        return clientHandlers;
    }

    @Override
    public void broadcastMessage(String message, String activeChannel) {
        clientHandlers.stream()
                .filter(clientHandler -> !clientHandler.getUsername().equals(DEFAULT_USERNAME))
                .filter(clientHandler -> clientHandler.getActiveChannel().equals(activeChannel))
                .forEach(clientHandler -> clientHandler.printMessage(message));
    }

    @Override
    public void cacheMessage(String username, String message, String activeChannel) {
        clientHandlers.stream()
                .filter(clientHandler -> !clientHandler.getUsername().equals(username))
                .filter(clientHandler -> clientHandler.getChannels().getAllChannels().contains(activeChannel))
                .filter(clientHandler -> !clientHandler.getActiveChannel().equals(activeChannel))
                .forEach(clientHandler -> clientHandler.getMessageCache().cacheMessageFromChannel(activeChannel, message));
    }

    @Override
    public String getAllUsernames() {
        return clientHandlers.stream()
                .map(ClientHandler::getUsername)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.joining(" "));
    }

    @Override
    public String getAllChannelUsernames(String activeChannel) {
        return clientHandlers.stream()
                .filter(clientHandler -> clientHandler.getActiveChannel().equalsIgnoreCase(activeChannel))
                .map(ClientHandler::getUsername)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.joining(" "));
    }

    @Override
    public String getAllChannelNames() {
        return clientHandlers.stream()
                .flatMap(clientHandler -> clientHandler.getChannels().getAllChannels().stream())
                .distinct()
                .sorted()
                .collect(Collectors.joining(" "));
    }

    @Override
    public boolean isUsernameUsed(String inputUsername) {
        return clientHandlers.stream()
                .map(ClientHandler::getUsername)
                .collect(Collectors.toSet())
                .contains(inputUsername);
    }

}
