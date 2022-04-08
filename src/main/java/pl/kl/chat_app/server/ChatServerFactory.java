package pl.kl.chat_app.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReadWriteLock;

public interface ChatServerFactory {

    ExecutorService createCachedThreadPool();

    ClientHandlers createClientHandlers();

    Channels createChannels();

    ChannelMessageCache createChannelMessageCache();

    ReadWriteLock createReadWriteLock();

}
