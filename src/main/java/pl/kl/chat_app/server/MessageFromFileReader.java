package pl.kl.chat_app.server;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class MessageFromFileReader {

    private final static String CHAT_HISTORY_FILE_PATH = ".\\ChatHistory\\ChatHistory.csv";
    private static final String SPLIT_REGEX = ",";
    private static final String USERNAMES_SPLIT_REGEX = " ";
    private static final int SPLIT_LIMIT = 5;

    public List<String> getClientMessageHistory(String username, String channel) {
        List<String> chatHistory = new ArrayList<>();
        final File file = new File(CHAT_HISTORY_FILE_PATH);
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                final String[] messageSplit = line.split(SPLIT_REGEX, SPLIT_LIMIT);
                final String timeSent = messageSplit[0];
                final String sentBy = messageSplit[1];
                final String sentToChannel = messageSplit[2];
                final List<String> sentToClients = Arrays.asList(messageSplit[3].split(USERNAMES_SPLIT_REGEX));
                final String content = messageSplit[4];
                if (sentToClients.contains(username) && sentToChannel.equals(channel)) {
                    final String messageFormat = String.format("%s: %s -> %s: %s", timeSent, sentBy, sentToChannel, content);
                    chatHistory.add(messageFormat);
                }
            }
        } catch (FileNotFoundException e) {
            log.error("File with chat history not found: " + e.getMessage());
        } catch (IOException e) {
            log.error("Reading messages from file failed: " + e.getMessage());
        }
        return chatHistory;
    }

}
