package pl.kl.chat_app.server;

public interface MessageCache {

    void addMessageToCache(String message);

    String getLastCachedMessage();

}
