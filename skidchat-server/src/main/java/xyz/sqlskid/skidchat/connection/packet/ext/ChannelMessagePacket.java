package xyz.sqlskid.skidchat.connection.packet.ext;

import xyz.sqlskid.skidchat.SkidChatServer;
import xyz.sqlskid.skidchat.channel.Channel;
import xyz.sqlskid.skidchat.client.Client;
import xyz.sqlskid.skidchat.connection.packet.Packet;
import xyz.sqlskid.skidchat.connection.packet.PacketContext;
import xyz.sqlskid.skidchat.connection.packet.PacketException;
import xyz.sqlskid.skidchat.message.Message;
import xyz.sqlskid.skidchat.util.CorrectionUtil;

public class ChannelMessagePacket extends Packet {

    public ChannelMessagePacket() {
        super(3, 2, 4);
    }

    @Override
    public void readPacket(SkidChatServer skidChatServer, Client client, PacketContext context) throws PacketException {
        String channelName = context.readString();
        String messageContent = context.readString();

        Channel channel = skidChatServer.getChannelManager().getChannelByName(channelName);

        if (channel == null) {
            return;
        }

        String messageSender = client.getUser().getUsername();

        messageContent = CorrectionUtil.sanitize(messageContent);

        Message message = new Message(messageSender, messageContent);
        channel.addMessage(message);
    }

    @Override
    public void writePacket(SkidChatServer skidChatServer, Client client, Object... params) throws PacketException {
        if (params.length != 4) {
            throw new PacketException("ChannelMessagePacket requires exactly 2 parameters: channelName, messageSender, messageContent and timestmap.");
        }

        String channelName = (String) params[0];
        String messageSender = (String) params[1];
        String messageContent = (String) params[2];
        long timestamp = (long) params[3];

        PacketContext context = new PacketContext(this);
        context.writeString(channelName);
        context.writeString(messageSender);
        context.writeString(messageContent);
        context.writeLong(timestamp);
        context.finish();

        skidChatServer.getPacketManager().sendPacket(client, context);
    }
}
