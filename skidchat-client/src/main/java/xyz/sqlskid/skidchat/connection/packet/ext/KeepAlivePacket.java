package xyz.sqlskid.skidchat.connection.packet.ext;

import xyz.sqlskid.skidchat.SkidChatClient;
import xyz.sqlskid.skidchat.connection.packet.Packet;
import xyz.sqlskid.skidchat.connection.packet.PacketContext;
import xyz.sqlskid.skidchat.connection.packet.PacketException;

public class KeepAlivePacket extends Packet {

    public KeepAlivePacket() {
        super(0, 0, 1);
    }

    @Override
    public void writePacket(SkidChatClient client, Object... params) throws PacketException {
        PacketContext context = new PacketContext(this);
        context.writeLong(System.currentTimeMillis());
        context.finish();
        client.getPacketManager().sendPacket(context);
    }
}
