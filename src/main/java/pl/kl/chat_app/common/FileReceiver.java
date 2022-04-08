package pl.kl.chat_app.common;

import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

@Slf4j
public class FileReceiver {

    private DataInputStream receiver;

    public FileReceiver(Socket clientSocket) {
        try {
            this.receiver = new DataInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            log.info("Creating input stream failed: " + e.getMessage());
        }
    }

    public void receive(String filePath) throws IOException {
        String fileName = "unknown";
        byte[] fileContentBytes = new byte[0];

        final int fileNameLength = receiver.readInt();

        if (fileNameLength > 0) {
            byte[] fileNameBytes = new byte[fileNameLength];
            receiver.readFully(fileNameBytes, 0, fileNameBytes.length);

            fileName = new String(fileNameBytes);
            int fileNameContentLength = receiver.readInt();

            if (fileNameContentLength > 0) {
                fileContentBytes = new byte[fileNameContentLength];
                receiver.readFully(fileContentBytes, 0, fileNameContentLength);
            }

            File fileToDownload = new File(filePath + fileName);
            fileToDownload.getParentFile().mkdirs();

            FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);
            fileOutputStream.write(fileContentBytes);
            fileOutputStream.close();
        }
    }

    public void close() {
        try {
            receiver.close();
        } catch (IOException e) {
            log.error("Closing input stream failed: " + e.getMessage());
        }
    }

}
