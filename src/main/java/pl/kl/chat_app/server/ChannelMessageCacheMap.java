package pl.kl.chat_app.server;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ChannelMessageCacheMap implements ChannelMessageCache {

    private static final int MAX_CACHED_CHANNELS = 5;
    private Map<String, MessageCache> cache;

    public ChannelMessageCacheMap() {
        this.cache = new LinkedHashMap<>(MAX_CACHED_CHANNELS) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, MessageCache> eldest) {
                return size() > MAX_CACHED_CHANNELS;
            }
        };
    }

    @Override
    public Optional<MessageCache> getCachedMessagesFromChannel(String channel) {
        return Optional.ofNullable(cache.get(channel));
    }

    @Override
    public void cacheMessageFromChannel(String channel, String message) {
        if (Objects.nonNull(cache.get(channel))) {
            final MessageCache messages = cache.get(channel);
            messages.addMessageToCache(message);
            cache.put(channel, messages);
        } else {
            final MessageCache messages = new MessageCacheList();
            messages.addMessageToCache(message);
            cache.put(channel, messages);
        }
    }

    @Override
    public void removeCachedChannel(String channel) {
        if (cache.containsKey(channel)) {
            cache.remove(channel);
        }
    }

}
