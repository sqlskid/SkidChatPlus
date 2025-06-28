package xyz.sqlskid.skidchat.connection.packet.ext;

import xyz.sqlskid.skidchat.SkidChatClient;
import xyz.sqlskid.skidchat.channel.Channel;
import xyz.sqlskid.skidchat.connection.packet.Packet;
import xyz.sqlskid.skidchat.connection.packet.PacketContext;
import xyz.sqlskid.skidchat.connection.packet.PacketException;
import xyz.sqlskid.skidchat.message.Message;
import xyz.sqlskid.skidchat.util.CompressionUtil;

import java.util.ArrayList;
import java.util.Base64;

public class ChannelMessageListPacket extends Packet {

    public ChannelMessageListPacket() {
        super(4, 2, 0);
    }

    @Override
    public void readPacket(SkidChatClient client, PacketContext context) throws PacketException {
        String channelName = context.readString();
        String compressedMessages = context.readString();

        if (channelName == null || compressedMessages == null) {
            throw new PacketException("ChannelMessageListPacket requires channelName and compressedMessages.");
        }

        Channel channel = client.getChannelManager().getChannelByName(channelName);
        if (channel == null) {
            throw new PacketException("Channel " + channelName + " does not exist.");
        }

        String decompressedData = CompressionUtil.decompress(Base64.getDecoder().decode(compressedMessages));
        String[] messages = decompressedData.split(",");

        ArrayList<Message> messageList = new ArrayList<>();

        for(String messageData: messages){
            messageList.add(Message.deserialize(messageData));
        }

        channel.addMessages(messageList);

        if(client.getChatFrame() != null) {
            client.getChatFrame().refreshChannel();
        }
    }


}
