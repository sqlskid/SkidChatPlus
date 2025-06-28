package xyz.sqlskid.skidchat.channel;

import xyz.sqlskid.skidchat.SkidChatClient;

import java.util.ArrayList;
import java.util.List;

public class ChannelManager {

    private List<Channel> channels;

    private SkidChatClient skidChatClient;

    public ChannelManager(SkidChatClient skidChatClient) {
        this.skidChatClient = skidChatClient;
        this.channels = new ArrayList<>();
    }

    public void addChannel(Channel channel) {
        channels.add(channel);
        skidChatClient.getChatFrame().refreshChannelList();
    }

    public void removeChannel(Channel channel) {
        channels.remove(channel);
    }

    public List<Channel> getChannels() {
        return new ArrayList<>(channels); // Return a copy to prevent external modification
    }

    public Channel getChannelByName(String name) {
        return channels.stream()
                .filter(channel -> channel.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public void clearChannels() {
        channels.clear();
    }
}
