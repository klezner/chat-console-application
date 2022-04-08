package pl.kl.chat_app.server;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;

@Slf4j
public class MessageToFileWriter {

    private final static String CHAT_HISTORY_FILE_PATH = ".\\ChatHistory\\ChatHistory.csv";

    public void archiveMessage(String username, String message, String activeChannel, String channelUsernames) {
        final File file = new File(CHAT_HISTORY_FILE_PATH);
        file.getParentFile().mkdirs();
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file, true))) {
            final String fileMessageFormat = String.format("%s,%s,%s,%s,%s%n", Timestamp.from(Instant.now()), username, activeChannel, channelUsernames, message);
            fileWriter.write(fileMessageFormat);
            fileWriter.flush();
        } catch (IOException e) {
            log.error("Writing message to file failed: " + e.getMessage());
        }
    }

}
