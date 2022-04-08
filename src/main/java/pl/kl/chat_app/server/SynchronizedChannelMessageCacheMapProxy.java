package pl.kl.chat_app.server;

import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;

public class SynchronizedChannelMessageCacheMapProxy implements ChannelMessageCache {

    private final ChatServerFactory factory = new MainChatServerFactory();
    private final ChannelMessageCache channelMessageCache;
    private final ReadWriteLock lock = factory.createReadWriteLock();

    public SynchronizedChannelMessageCacheMapProxy(ChannelMessageCache channelMessageCache) {
        this.channelMessageCache = channelMessageCache;
    }

    @Override
    public Optional<MessageCache> getCachedMessagesFromChannel(String channel) {
        lock.readLock().lock();
        final Optional<MessageCache> cachedMessagesFromChannel = channelMessageCache.getCachedMessagesFromChannel(channel);
        lock.readLock().unlock();
        return cachedMessagesFromChannel;
    }

    @Override
    public void cacheMessageFromChannel(String channel, String message) {
        lock.writeLock().lock();
        channelMessageCache.cacheMessageFromChannel(channel, message);
        lock.writeLock().unlock();

    }

    @Override
    public void removeCachedChannel(String channel) {
        lock.writeLock().lock();
        channelMessageCache.removeCachedChannel(channel);
        lock.writeLock().unlock();
    }

}
