package xyz.sqlskid.skidchat.connection.packet.ext;

import xyz.sqlskid.skidchat.SkidChatClient;
import xyz.sqlskid.skidchat.connection.packet.Packet;
import xyz.sqlskid.skidchat.connection.packet.PacketContext;
import xyz.sqlskid.skidchat.connection.packet.PacketException;

public class DisconnectPacket extends Packet {

    public DisconnectPacket() {
        super(5, 1, 0);
    }

    @Override
    public void readPacket(SkidChatClient skidChatClient, PacketContext context) throws PacketException {
        String reason = context.readString();

        // Handle disconnection logic here
        skidChatClient.disconnect(reason);
    }
}
