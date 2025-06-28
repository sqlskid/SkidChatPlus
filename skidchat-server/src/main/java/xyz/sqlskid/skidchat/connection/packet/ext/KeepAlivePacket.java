package xyz.sqlskid.skidchat.connection.packet.ext;

import xyz.sqlskid.skidchat.SkidChatServer;
import xyz.sqlskid.skidchat.client.Client;
import xyz.sqlskid.skidchat.connection.packet.Packet;
import xyz.sqlskid.skidchat.connection.packet.PacketContext;
import xyz.sqlskid.skidchat.connection.packet.PacketException;

public class KeepAlivePacket extends Packet {

    public KeepAlivePacket() {
        super(0, 1, 0);
    }

    @Override
    public void readPacket(SkidChatServer skidChatServer, Client client, PacketContext context) throws PacketException {
        if(client.getUser() == null){
            client.disconnect("Not logged in.");
            return;
        }

        client.setLastKeepAlive(System.currentTimeMillis());
    }
}
