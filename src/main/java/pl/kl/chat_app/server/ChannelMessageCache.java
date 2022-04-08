package pl.kl.chat_app.server;

import java.util.Optional;

public interface ChannelMessageCache {

    Optional<MessageCache> getCachedMessagesFromChannel(String channel);

    void cacheMessageFromChannel(String channel, String message);

    void removeCachedChannel(String channel);

}
