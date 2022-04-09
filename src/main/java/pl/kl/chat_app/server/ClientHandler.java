package pl.kl.chat_app.server;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import pl.kl.chat_app.common.Actions;
import pl.kl.chat_app.common.FileReceiver;
import pl.kl.chat_app.common.FileSender;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class ClientHandler implements Runnable {

    private static final String SERVER_PATH = ".\\ServerUploads\\";
    private static final String DEFAULT_USERNAME = "unknown";
    private static final String DEFAULT_CHANNEL = "general";
    private static final String CMD_PREFIX = "/";
    private static final String SPLIT_REGEX = " ";
    private static final int MIN_SPLIT_LIMIT = 2;
    private static final int MAX_SPLIT_LIMIT = 3;
    private final ChatServerFactory factory = new MainChatServerFactory();
    private final Socket clientSocket;
    private final ClientHandlers clientHandlers;
    private final MessageToFileWriter fileWriter;
    private final MessageFromFileReader fileReader;
    private final FileSender fileSender;
    private final FileReceiver fileReceiver;
    @Getter
    private final Channels channels = factory.createChannels();
    @Getter
    private final ChannelMessageCache messageCache = factory.createChannelMessageCache();
    private BufferedReader socketReader;
    private PrintWriter socketWriter;
    @Getter
    private String username;
    @Getter
    private String activeChannel;

    public ClientHandler(Socket clientSocket, ClientHandlers clientHandlers) {
        this.clientSocket = clientSocket;
        this.clientHandlers = clientHandlers;
        this.username = DEFAULT_USERNAME;
        this.activeChannel = DEFAULT_CHANNEL;
        this.fileWriter = new MessageToFileWriter();
        this.fileReader = new MessageFromFileReader();
        this.fileSender = new FileSender(clientSocket);
        this.fileReceiver = new FileReceiver(clientSocket);
        try {
            this.socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.socketWriter = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            log.error("Creating input and output stream failed: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            handleLogin();
            handleAction();
        } catch (IOException e) {
            closeSocket();
        }
    }

    private void handleAction() throws IOException {
        String message;
        while ((message = socketReader.readLine()) != null) {
            if (message.startsWith(CMD_PREFIX)) {
                final String[] splitMessage = message.split(SPLIT_REGEX, MAX_SPLIT_LIMIT);
                final String command = splitMessage[0];
                if (Actions.CLOSE_CONNECTION.getInput().equalsIgnoreCase(command) && splitMessage.length == 1) {
                    handleLogout();
                } else if (Actions.HELP.getInput().equalsIgnoreCase(command) && splitMessage.length == 1) {
                    handleHelp();
                } else if (Actions.ME.getInput().equalsIgnoreCase(command) && splitMessage.length == 1) {
                    handleAboutMe();
                } else if (Actions.ALL_USERS.getInput().equalsIgnoreCase(command) && splitMessage.length == 1) {
                    handleAllUsernamesGet();
                } else if (Actions.ALL_CHANNELS.getInput().equalsIgnoreCase(command) && splitMessage.length == 1) {
                    handleAllChannelsGet();
                } else if (Actions.ALL_FILES.getInput().equalsIgnoreCase(command) && splitMessage.length == 1) {
                    handleServerFiles();
                } else if (Actions.CHAT_HISTORY.getInput().equalsIgnoreCase(command) && splitMessage.length == 1) {
                    handleMyChatHistory();
                } else if (Actions.CHANNEL.getInput().equalsIgnoreCase(command) && splitMessage.length >= 1) {
                    final String commandOption = splitMessage[1];
                    if (Actions.ALL_USERS.getInput().equalsIgnoreCase(commandOption) && splitMessage.length == 2) {
                        handleChannelUsernamesGet();
                    } else if (Actions.CHANNEL_LEAVE.getInput().equalsIgnoreCase(commandOption) && splitMessage.length == 2) {
                        handleChannelLeave();
                    } else if (Actions.CHANNEL_JOIN.getInput().equalsIgnoreCase(commandOption) && splitMessage.length == 3) {
                        final String channelName = splitMessage[2];
                        handleChannelCreateOrJoin(channelName);
                    } else {
                        handleInvalidChannelAction();
                    }
                } else if (Actions.UPLOAD_FILE.getInput().equalsIgnoreCase(command) && splitMessage.length == 2) {
                    final String filePath = splitMessage[1];
                    handleFileUpload(filePath);
                } else if (Actions.DOWNLOAD_FILE.getInput().equalsIgnoreCase(command) && splitMessage.length == 2) {
                    handleFileDownload(message);
                } else {
                    handleInvalidAction();
                }
            } else {
                handleMessageSend(message);
            }
        }
    }

    private void handleMyChatHistory() {
        final List<String> clientMessageHistory = fileReader.getClientMessageHistory(username, activeChannel);
        if (!clientMessageHistory.isEmpty()) {
            clientMessageHistory.forEach(this::printMessage);
        } else {
            final String messageFormat = String.format("No such history for %s and %s channel", username, activeChannel);
            printMessage(messageFormat);
        }
    }

    private void handleFileDownload(String message) throws IOException {
        final String fileName = message.split(SPLIT_REGEX, MIN_SPLIT_LIMIT)[1];
        if (fileSender.ifFileExists(fileName)) {
            log.info("{} has started to downloading file {}", username, fileName);
            printMessage(message);
            fileSender.send(SERVER_PATH + fileName);
            log.info("File {} has been downloaded by {}", fileName, username);
            final String messageFormat = String.format("Server -> File %s has been downloaded", fileName);
            printMessage(messageFormat);
        } else {
            printMessage("Server -> File does not exists. Try with another filename");
        }
    }

    private void handleFileUpload(String fileName) throws IOException {
        log.info("{} has started to uploading file {}", username, fileName);
        fileReceiver.receive(SERVER_PATH);
        log.info("File {} has been uploaded by {}", fileName, username);
        final String messageFormat = String.format("Server -> File %s has been uploaded by %s", fileName, username);
        clientHandlers.broadcastMessage(messageFormat, activeChannel);
        clientHandlers.cacheMessage(username, messageFormat, activeChannel);
        saveMessage(messageFormat);
    }

    private void handleServerFiles() {
        final List<String> serverUploadsFileNamesList = Arrays.asList(new File(SERVER_PATH).list());
        if (serverUploadsFileNamesList.isEmpty()) {
            final String messageFormat = "Server -> There are no files uploaded on server";
            printMessage(messageFormat);
        }
        final String allServerUploadsFileNames = String.join(" ", serverUploadsFileNamesList);
        final String messageFormat = String.format("Server -> All files uploaded on server: %s", allServerUploadsFileNames);
        printMessage(messageFormat);
    }

    private void syncChannel() {
        final MessageCache cachedMessages = messageCache.getCachedMessagesFromChannel(activeChannel)
                .orElse(new MessageCacheList());
        String message;
        while ((message = cachedMessages.getLastCachedMessage()) != null) {
            printMessage(message);
        }
    }

    private void handleAllChannelsGet() {
        final String allChannels = clientHandlers.getAllChannelNames();
        final String messageFormat = String.format("Server -> All channels: %s", allChannels);
        printMessage(messageFormat);
    }

    private void handleAboutMe() {
        final String myChannels = getMyChannels();
        final String messageFormat = String.format("Server -> Username: %s | Subscribed channels: %s | Active channel: %s",
                username, myChannels, activeChannel);
        printMessage(messageFormat);
    }

    private String getMyChannels() {
        return String.join(" ", channels.getAllChannels());
    }

    private void handleInvalidAction() {
        final String messageFormat = "Server -> Invalid action";
        printMessage(messageFormat);
    }

    private void handleInvalidChannelAction() {
        final String messageFormat = "Server -> Invalid channel action";
        printMessage(messageFormat);
    }

    private void handleChannelLeave() {
        if (!activeChannel.equalsIgnoreCase(DEFAULT_CHANNEL)) {
            final String leaveMessageFormat = String.format("Server -> %s has left %s channel", username, activeChannel);
            clientHandlers.broadcastMessage(leaveMessageFormat, activeChannel);
            log.info("{} has left {} channel", username, activeChannel);
            removeCachedChannel();
            channels.removeChannel(activeChannel);
            activeChannel = DEFAULT_CHANNEL;
            log.info("{} has joined {} channel", username, activeChannel);
            final String joinMessageFormat = String.format("Server -> %s has joined %s channel", username, activeChannel);
            clientHandlers.broadcastMessage(joinMessageFormat, activeChannel);
            syncChannel();
        } else {
            final String messageFormat = String.format("Server -> Cannot leave %s channel", DEFAULT_CHANNEL);
            printMessage(messageFormat);
        }
    }

    private void removeCachedChannel() {
        messageCache.removeCachedChannel(activeChannel);
    }

    private void handleChannelUsernamesGet() {
        final String channelUsernames = clientHandlers.getAllChannelUsernames(activeChannel);
        final String messageFormat = String.format("Server -> All channel usernames: %s", channelUsernames);
        printMessage(messageFormat);
    }

    private void handleChannelCreateOrJoin(String channel) {
        if (channel.isEmpty() || channel.isBlank()) {
            printMessage("Server -> Channel you have entered is empty, please enter not empty channnel");
        } else {
            activeChannel = channel.trim();
            channels.addChannel(activeChannel);
            log.info("{} has joined {} channel", username, activeChannel);
            final String messageFormat = String.format("Server -> %s has joined %s channel", username, activeChannel);
            clientHandlers.broadcastMessage(messageFormat, activeChannel);
            syncChannel();
        }
    }

    private void handleHelp() {
        final String messageFormat = String.format("Available commands:%n" +
                        "%s -> about me (username, subscribed channels, active channel)%n" +
                        "%s -> get all users connected%n" +
                        "%s -> get all active channels%n" +
                        "%s -> get all my channel history%n" +
                        "%s %s channel_name -> create and join new channels or join channel if exists (still subscribed)%n" +
                        "%s %s -> leave channel (unsubscribe) and delete if there is no clients%n" +
                        "%s %s -> get all channel users (subscribing channel)%n" +
                        "%s file_name -> upload file to server%n" +
                        "%s file_name -> download file from server%n" +
                        "%s -> disconnect server and close client",
                Actions.ME.getInput(), Actions.ALL_USERS.getInput(), Actions.ALL_CHANNELS.getInput(),
                Actions.CHANNEL.getInput(), Actions.CHAT_HISTORY.getInput(), Actions.CHANNEL_JOIN.getInput(),
                Actions.CHANNEL.getInput(), Actions.CHANNEL_LEAVE.getInput(), Actions.CHANNEL.getInput(),
                Actions.ALL_USERS.getInput(), Actions.UPLOAD_FILE.getInput(), Actions.DOWNLOAD_FILE.getInput(),
                Actions.CLOSE_CONNECTION.getInput());
        printMessage("Server -> " + messageFormat);
    }

    private void handleAllUsernamesGet() {
        final String allUsernames = clientHandlers.getAllUsernames();
        final String messageFormat = String.format("All connected users: %s", allUsernames);
        printMessage("Server -> " + messageFormat);
    }

    private void handleLogout() {
        final String messageFormat = String.format("Server -> %s has disconnected", username);
        clientHandlers.broadcastMessage(messageFormat, activeChannel);
        log.info("{} has disconnected", username);
        clientHandlers.remove(this);
        closeSocket();
    }

    private void handleMessageSend(String message) {
        final String messageFormat = String.format("%s -> %s: %s", username, activeChannel, message);
        clientHandlers.broadcastMessage(messageFormat, activeChannel);
        clientHandlers.cacheMessage(username, messageFormat, activeChannel);
        saveMessage(message);
    }

    private void handleLogin() throws IOException {
        printMessage("Server -> Welcome, for more info type: /help\nPlease enter your username: ");
        String tempUsername = socketReader.readLine();
        while (isUsernameUsed(tempUsername) || tempUsername.isEmpty() || tempUsername.isBlank()) {
            if (isUsernameUsed(tempUsername)) {
                printMessage("Server -> Username you have entered is used by another user, please enter different username:");
                tempUsername = socketReader.readLine();
            } else if (tempUsername.isEmpty()) {
                printMessage("Server -> Username you have entered is empty, please enter not empty username:");
                tempUsername = socketReader.readLine();
            } else if (tempUsername.isBlank()) {
                printMessage("Server -> Username you entered is blank, please enter not blank username:");
                tempUsername = socketReader.readLine();
            }
        }
        username = tempUsername.trim();
        final String messageFormat = String.format("Server -> %s has connected and has joined %s channel", username, activeChannel);
        clientHandlers.broadcastMessage(messageFormat, activeChannel);
        log.info("{} has connected and has joined {} channel", username, activeChannel);
    }

    private boolean isUsernameUsed(String username) {
        return clientHandlers.isUsernameUsed(username);
    }

    public void saveMessage(String message) {
        final String channelUsernames = clientHandlers.getAllChannelUsernames(activeChannel);
        fileWriter.archiveMessage(username, message, activeChannel, channelUsernames);
    }

    public void printMessage(String message) {
        socketWriter.println(message);
    }

    public void closeSocket() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
