package xyz.sqlskid.skidchat.connection.packet.ext;

import xyz.sqlskid.skidchat.SkidChatClient;
import xyz.sqlskid.skidchat.connection.packet.Packet;
import xyz.sqlskid.skidchat.connection.packet.PacketContext;
import xyz.sqlskid.skidchat.connection.packet.PacketException;
import xyz.sqlskid.skidchat.message.Message;

public class ChannelMessagePacket extends Packet  {
    public ChannelMessagePacket() {
        super(3, 4, 2);
    }

    @Override
    public void readPacket(SkidChatClient client, PacketContext context) throws PacketException {
        String channelName = context.readString();
        String messageSender = context.readString();
        String messageContent = context.readString();
        long timestamp = context.readLong();

        if (client.getChannelManager().getChannelByName(channelName) == null) {
            throw new PacketException("Channel " + channelName + " does not exist.");
        }

        Message message = new Message(messageSender, messageContent, timestamp);
        client.getChannelManager().getChannelByName(channelName).addMessage(message);
        client.getChatFrame().appendMessage(message, channelName);

    }

    @Override
    public void writePacket(SkidChatClient client, Object... params) throws PacketException {
        if (params.length != 2) {
            throw new PacketException("ChannelMessagePacket requires exactly 2 parameters: channelName and messageContent.");
        }

        String channelName = (String) params[0];
        String messageContent = (String) params[1];

        PacketContext context = new PacketContext(this);
        context.writeString(channelName);
        context.writeString(messageContent);
        context.finish();

        client.getPacketManager().sendPacket(context);
    }
}
