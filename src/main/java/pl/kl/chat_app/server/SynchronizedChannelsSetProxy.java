package pl.kl.chat_app.server;

import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;

public class SynchronizedChannelsSetProxy implements Channels {

    private final ChatServerFactory factory = new MainChatServerFactory();
    private final Channels channels;
    private final ReadWriteLock lock = factory.createReadWriteLock();

    public SynchronizedChannelsSetProxy(Channels channels) {
        this.channels = channels;
    }

    @Override
    public void addChannel(String channel) {
        lock.writeLock().lock();
        channels.addChannel(channel);
        lock.writeLock().unlock();
    }

    @Override
    public void removeChannel(String channel) {
        lock.writeLock().lock();
        channels.removeChannel(channel);
        lock.writeLock().unlock();
    }

    @Override
    public Set<String> getAllChannels() {
        lock.readLock().lock();
        final Set<String> channelsSet = this.channels.getAllChannels();
        lock.readLock().unlock();
        return channelsSet;
    }

}
