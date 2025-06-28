package xyz.sqlskid.skidchat.connection.packet.ext;

import xyz.sqlskid.skidchat.SkidChatServer;
import xyz.sqlskid.skidchat.channel.Channel;
import xyz.sqlskid.skidchat.client.Client;
import xyz.sqlskid.skidchat.connection.packet.Packet;
import xyz.sqlskid.skidchat.connection.packet.PacketContext;
import xyz.sqlskid.skidchat.connection.packet.PacketException;
import xyz.sqlskid.skidchat.message.Message;
import xyz.sqlskid.skidchat.util.CompressionUtil;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.StringJoiner;

public class ChannelMessageListPacket extends Packet {

    public ChannelMessageListPacket() {
        super(4, 0, 2);
    }

    @Override
    public void readPacket(SkidChatServer skidChatServer, Client client, PacketContext context) throws PacketException {

    }

    @Override
    public void writePacket(SkidChatServer skidChatServer, Client client, Object... params) throws PacketException {
        if (params.length != 1) {
            throw new PacketException("ChannelMessageListPacket requires exactly 1 parameter: channelName.");
        }

        String channelName = (String) params[0];

        Channel channel = skidChatServer.getChannelManager().getChannelByName(channelName);

        if(channel == null) {
            throw new PacketException("Channel " + channelName + " does not exist.");
        }

        if(channel.getMessages().isEmpty()) return;

        ArrayList<String> serializedMessages = new ArrayList<>();

        List<Message> messageList = channel.getMessages();

        // Select only the last 100 messages to avoid sending too much data
        if(messageList.size() > 100) {
            messageList = messageList.subList(Math.max(0, messageList.size() - 100), messageList.size());
        }

        for(Message message : messageList) {
            serializedMessages.add(message.serialize());
        }

        StringJoiner joiner = new StringJoiner(",");
        for(String serializedMessage : serializedMessages) {
            joiner.add(serializedMessage);
        }

        String compressedMessages = Base64.getEncoder().encodeToString(CompressionUtil.compress(joiner.toString()));

        PacketContext context = new PacketContext(this);
        context.writeString(channelName);
        context.writeString(compressedMessages);
        context.finish();

        skidChatServer.getPacketManager().sendPacket(client, context);
    }
}
