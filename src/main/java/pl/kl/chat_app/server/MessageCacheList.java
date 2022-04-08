package pl.kl.chat_app.server;

import java.util.LinkedList;
import java.util.Queue;

public class MessageCacheList implements MessageCache {

    private static final int MAX_CACHED_MESSAGES_BY_CHANNEL = 10;
    private final Queue<String> messages;

    public MessageCacheList() {
        this.messages = new LinkedList<>();
    }

    @Override
    public void addMessageToCache(String message) {
        if (messages.size() < MAX_CACHED_MESSAGES_BY_CHANNEL) {
            messages.offer(message);
        } else {
            messages.poll();
            messages.offer(message);
        }
    }

    @Override
    public String getLastCachedMessage() {
        return messages.poll();
    }

}
