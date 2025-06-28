package xyz.sqlskid.skidchat.connection.packet.ext;

import xyz.sqlskid.skidchat.SkidChatClient;
import xyz.sqlskid.skidchat.connection.packet.Packet;
import xyz.sqlskid.skidchat.connection.packet.PacketContext;
import xyz.sqlskid.skidchat.connection.packet.PacketException;

import javax.swing.*;

public class LoginPacket extends Packet {

    public LoginPacket() {
        super(1, 2, 3);
    }

    @Override
    public void readPacket(SkidChatClient client, PacketContext context) throws PacketException {
        String status = context.readString();

        if(client.isLoggedIn()) // Already logged in, skipping
            return;

        switch (status) {
            case "success":
                String serverName = context.readString();
                client.getChatFrame().setVisible(true);
                client.setLoggedIn(true);
                //Show the server name
                JOptionPane.showMessageDialog(
                        client.getChatFrame(),
                        "Successfully logged in to " + serverName + "!",
                        "Login Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
                client.getChatFrame().setTitle("SkidChat - " + serverName);
                client.getKeepAliveWorker().start();
                break;
            case "error":
                String errorMessage = context.readString();
                throw new PacketException("Login error: " + errorMessage);
        }
    }

    @Override
    public void writePacket(SkidChatClient client, Object... params) throws PacketException {
        if (params.length != 2) {
            throw new PacketException("LoginPacket requires exactly 2 parameters: username and password.");
        }

        PacketContext context = new PacketContext(this);
        context.writeString((String) params[0]); // username
        context.writeString((String) params[1]); // password
        context.writeString(SkidChatClient.VERSION);
        context.finish();
        client.getPacketManager().sendPacket(context);
    }
}
