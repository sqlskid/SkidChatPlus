package xyz.sqlskid.skidchat.connection.packet.ext;

import xyz.sqlskid.skidchat.SkidChatServer;
import xyz.sqlskid.skidchat.client.Client;
import xyz.sqlskid.skidchat.connection.packet.Packet;
import xyz.sqlskid.skidchat.connection.packet.PacketContext;
import xyz.sqlskid.skidchat.connection.packet.PacketException;

public class DisconnectPacket extends Packet {

    public DisconnectPacket() {
        super(5, 0, 1);
    }

    @Override
    public void writePacket(SkidChatServer skidChatServer, Client client, Object... params) throws PacketException {
        if (params.length < 1 || !(params[0] instanceof String)) {
            throw new PacketException("DisconnectPacket requires a reason string as a parameter.");
        }

        String reason = (String) params[0];
        PacketContext context = new PacketContext(this);
        context.writeString(reason);
        context.finish();

        // Send the disconnect packet to the client
        skidChatServer.getPacketManager().sendPacket(client, context);
    }
}
