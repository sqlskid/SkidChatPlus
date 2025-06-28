package xyz.sqlskid.skidchat.connection.packet.ext;

import xyz.sqlskid.skidchat.SkidChatServer;
import xyz.sqlskid.skidchat.client.Client;
import xyz.sqlskid.skidchat.connection.packet.Packet;
import xyz.sqlskid.skidchat.connection.packet.PacketContext;
import xyz.sqlskid.skidchat.connection.packet.PacketException;

import java.util.ArrayList;

public class ChannelListPacket extends Packet {

    public ChannelListPacket() {
        super(2, 0, 1);
    }

    @Override
    public void readPacket(SkidChatServer skidChatServer, Client client, PacketContext context) throws PacketException {
        // Nope
    }

    @Override
    public void writePacket(SkidChatServer skidChatServer, Client client, Object... params) throws PacketException {
        if (params.length != 0) {
            throw new PacketException("ChannelListPacket does not accept parameters.");
        }

        PacketContext context = new PacketContext(this);
        ArrayList<String> channels = new ArrayList<>();
        channels.addAll(skidChatServer.getChannelManager().getChannels().stream()
                .map(channel -> channel.getName())
                .toList());
        context.writeList(channels);
        context.finish();
        skidChatServer.getPacketManager().sendPacket(client, context);
    }
}
