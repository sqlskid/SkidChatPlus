package xyz.sqlskid.skidchat.connection.packet;

import lombok.Getter;
import xyz.sqlskid.skidchat.SkidChatServer;
import xyz.sqlskid.skidchat.client.Client;
import xyz.sqlskid.skidchat.connection.packet.ext.*;

import java.util.ArrayList;
import java.util.List;

public class PacketManager {

    @Getter
    private List<Packet> packets;

    private SkidChatServer skidChatServer;

    public PacketManager(SkidChatServer skidChatServer) {
        this.skidChatServer = skidChatServer;
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

    public void handle(Client client, String rawData) throws PacketException {
        PacketContext context = new PacketContext(rawData);
        Packet packet = getPacketById(context.getPacketId());
        if (packet == null) {
            throw new PacketException("Unknown packet ID: " + context.getPacketId());
        }

        if(client.getUser() == null && (packet.getId() != Packets.LOGIN.getPacket().getId() && packet.getId() != Packets.KEY_EXCHANGE.getPacket().getId() && packet.getId() != Packets.ENCRYPTED.getPacket().getId())){
            System.err.println("Client " + client.getIdentifier() + " tried to send packet ID: " + context.getPacketId() + " without being logged in.");

            throw new PacketException("Client not logged in, cannot process packet ID: " + context.getPacketId());
        }

        packet.readPacket(skidChatServer, client, context);
    }

    public Packet getPacketById(int id){
        return packets.stream()
                .filter(packet -> packet.getId() == id)
                .findFirst()
                .orElse(null);
    }


    public void sendPacket(Client client, PacketContext context) {
        boolean clientExchangedKeys = client.getPublicKey() != null;
        if(!clientExchangedKeys || context.getPacketId() == Packets.KEY_EXCHANGE.getPacket().getId() || context.getPacketId() == Packets.ENCRYPTED.getPacket().getId())
            client.sendData(context.getRawData());
        else {
            try {
                Packets.ENCRYPTED.getPacket().writePacket(skidChatServer, client, context.getRawData());
            } catch (PacketException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
