package pl.kl.chat_app.server;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChannelsSet implements Channels {

    private static final String DEFAULT_CHANNEL = "general";
    private final Set<String> channels;

    public ChannelsSet() {
        this.channels = new HashSet<>(List.of(DEFAULT_CHANNEL));
    }

    @Override
    public void addChannel(String channel) {
        channels.add(channel);
    }

    @Override
    public void removeChannel(String channel) {
        channels.remove(channel);
    }

    @Override
    public Set<String> getAllChannels() {
        return new HashSet<>(channels);
    }

}
