package pl.kl.chat_app.server;

import java.util.Set;

public interface Channels {

    void addChannel(String channel);

    void removeChannel(String channel);

    Set<String> getAllChannels();

}
