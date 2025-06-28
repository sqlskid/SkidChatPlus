package xyz.sqlskid.skidchat.connection.packet;

import xyz.sqlskid.skidchat.SkidChatClient;

public enum Packets {
    KEEP_ALIVE(0),
    LOGIN(1),
    CHANNEL_LIST(2),
    CHANNEL_MESSAGE(3),
    CHANNEL_MESSAGE_LIST(4),
    DISCONNECT(5),
    KEY_EXCHANGE(900),
    ENCRYPTED(901);

    private int id;

    Packets(int id){
        this.id = id;
    }

    public Packet getPacket() {
        return SkidChatClient.getInstance().getPacketManager().getPacketById(id);
    }
}

