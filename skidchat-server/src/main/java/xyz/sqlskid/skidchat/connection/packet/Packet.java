package xyz.sqlskid.skidchat.connection.packet;

import lombok.Getter;
import xyz.sqlskid.skidchat.SkidChatServer;
import xyz.sqlskid.skidchat.client.Client;

public class Packet {

    public static String PACKET_DELIMITER = "\u001E";

    @Getter
    private int id;

    @Getter
    private int readParamCount;

    @Getter
    private int writeParamCount;

    public Packet(int id, int readParamCount, int writeParamCount) {
        this.id = id;
        this.readParamCount = readParamCount;
        this.writeParamCount = writeParamCount;
    }

    public void readPacket(SkidChatServer skidChatServer, Client client, PacketContext context) throws PacketException {
        // Default implementation does nothing
        // Override this method in subclasses to handle specific packet logic
        throw new PacketException("Packet with ID " + id + " does not implement readPacket method.");
    }

    public void writePacket(SkidChatServer skidChatServer, Client client, Object... params) throws PacketException {
        // Default implementation does nothing
        // Override this method in subclasses to handle specific packet logic
        throw new PacketException("Packet with ID " + id + " does not implement writePacket method.");
    }

}
