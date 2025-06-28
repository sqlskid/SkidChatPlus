package xyz.sqlskid.skidchat.connection.packet.ext;

import xyz.sqlskid.skidchat.SkidChatClient;
import xyz.sqlskid.skidchat.channel.Channel;
import xyz.sqlskid.skidchat.channel.ChannelManager;
import xyz.sqlskid.skidchat.connection.packet.Packet;
import xyz.sqlskid.skidchat.connection.packet.PacketContext;
import xyz.sqlskid.skidchat.connection.packet.PacketException;

import java.util.List;

public class ChannelListPacket extends Packet {

    public ChannelListPacket() {
        super(2, 1, 0);
    }

    @Override
    public void readPacket(SkidChatClient client, PacketContext context) throws PacketException {
        List<String> channels = context.readList();

        client.getChannelManager().clearChannels();

        if (channels == null || channels.isEmpty()) {
            throw new PacketException("Received empty channel list.");
        }

        for (String channel : channels) {
            if (channel == null || channel.isEmpty()) {
                continue;
            }

            Channel ch = new Channel(channel);
            client.getChannelManager().addChannel(ch);
        }
    }
}
