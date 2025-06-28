package xyz.sqlskid.skidchat.connection.packet;

import lombok.Getter;
import xyz.sqlskid.skidchat.SkidChatClient;
import xyz.sqlskid.skidchat.connection.SkidChatConnection;
import xyz.sqlskid.skidchat.connection.packet.ext.*;

import java.util.ArrayList;
import java.util.List;

public class PacketManager {

    @Getter
    private List<Packet> packets;

    private SkidChatClient skidChatClient;

    public PacketManager(SkidChatClient skidChatClient){
        this.skidChatClient = skidChatClient;
        this.packets = new ArrayList<>();
        registerPackets();
    }

    private void registerPackets() {
        packets.add(new KeepAlivePacket());
        packets.add(new LoginPacket());
        packets.add(new ChannelListPacket());
        packets.add(new ChannelMessagePacket());
        packets.add(new ChannelMessageListPacket());
        packets.add(new DisconnectPacket());
        packets.add(new KeyExchangePacket());
        packets.add(new EncryptedPacket());
    }

    public void handle(String rawData) throws PacketException {
        PacketContext context = new PacketContext(rawData);
        Packet packet = getPacketById(context.getPacketId());
        if (packet == null) {
            throw new PacketException("Unknown packet ID: " + context.getPacketId());
        }

        packet.readPacket(skidChatClient, context);
    }

    public Packet getPacketById(int id){
        return packets.stream()
                .filter(packet -> packet.getId() == id)
                .findFirst()
                .orElse(null);
    }


    public void sendPacket(PacketContext context) {

        SkidChatConnection connection = skidChatClient.getSkidChatConnection();
        boolean clientExchangedKeys = skidChatClient.getServerKey() != null;
        if(!clientExchangedKeys || context.getPacketId() == Packets.KEY_EXCHANGE.getPacket().getId() || context.getPacketId() == Packets.ENCRYPTED.getPacket().getId()) {
            connection.sendData(context.getRawData());
            return;
        }
        else{
            try {
                Packets.ENCRYPTED.getPacket().writePacket(skidChatClient, context.getRawData());
            } catch (PacketException e) {
                e.printStackTrace();
            }
        }

    }
}
