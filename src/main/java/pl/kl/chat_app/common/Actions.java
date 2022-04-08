package pl.kl.chat_app.common;

public enum Actions {
    ME("/me"),
    HELP("/help"),
    CHANNEL("/channel"),
    CHANNEL_JOIN("/join"),
    CHANNEL_LEAVE("/leave"),
    ALL_USERS("/users"),
    ALL_CHANNELS("/channels"),
    ALL_FILES("/files"),
    UPLOAD_FILE("/upload"),
    DOWNLOAD_FILE("/download"),
    CHAT_HISTORY("/history"),
    CLOSE_CONNECTION("/quit");

    private String input;

    Actions(String actionInput) {
        this.input = actionInput;
    }

    public String getInput() {
        return input;
    }
}
