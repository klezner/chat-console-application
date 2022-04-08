package pl.kl.chat_app.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MainChatServerFactory implements ChatServerFactory {

    @Override
    public ExecutorService createCachedThreadPool() {
        return Executors.newCachedThreadPool();
    }

    @Override
    public ClientHandlers createClientHandlers() {
        return new ClientHandlersList();
    }

    @Override
    public Channels createChannels() {
        return new SynchronizedChannelsSetProxy(new ChannelsSet());
    }

    @Override
    public ChannelMessageCache createChannelMessageCache() {
        return new SynchronizedChannelMessageCacheMapProxy(new ChannelMessageCacheMap());
    }

    @Override
    public ReadWriteLock createReadWriteLock() {
        return new ReentrantReadWriteLock();
    }

}
