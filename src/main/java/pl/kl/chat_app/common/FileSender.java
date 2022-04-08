package pl.kl.chat_app.common;

import lombok.extern.slf4j.Slf4j;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Slf4j
public class FileSender {

    private DataOutputStream sender;

    public FileSender(Socket clientSocket) {
        try {
            this.sender = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            log.info("Creating output stream failed: " + e.getMessage());
        }
    }

    public void send(String file) throws IOException {
        File fileToSend = new File(file);
        final FileInputStream fileInputStream = new FileInputStream(fileToSend);

        final String fileName = fileToSend.getName();
        final byte[] fileNameBytes = fileName.getBytes(StandardCharsets.UTF_8);

        final byte[] fileContentBytes = new byte[(int) fileToSend.length()];
        fileInputStream.read(fileContentBytes);

        sender.writeInt(fileNameBytes.length);
        sender.write(fileNameBytes);

        sender.writeInt(fileContentBytes.length);
        sender.write(fileContentBytes);

        fileInputStream.close();
    }

    public boolean ifFileExists(String filePath) {
        final File fileToSend = new File(filePath);
        return fileToSend.exists();
    }

    public void close() {
        try {
            sender.close();
        } catch (IOException e) {
            log.error("Closing output stream failed: " + e.getMessage());
        }
    }

}
