package xyz.sqlskid.skidchat.connection.packet.ext;

import xyz.sqlskid.skidchat.SkidChatServer;
import xyz.sqlskid.skidchat.channel.Channel;
import xyz.sqlskid.skidchat.client.Client;
import xyz.sqlskid.skidchat.connection.packet.*;
import xyz.sqlskid.skidchat.user.User;
import xyz.sqlskid.skidchat.util.CorrectionUtil;
import xyz.sqlskid.skidchat.util.HashUtil;

import java.util.Objects;

public class LoginPacket extends Packet {

    public LoginPacket() {
        super(1, 3, 3);
    }

    @Override
    public void readPacket(SkidChatServer skidChatServer, Client client, PacketContext context) throws PacketException {
        String username = context.readString();
        String password = context.readString();
        String hashedPassword = HashUtil.MD5(username + password);
        String version = context.readString();

        if (!version.equals(SkidChatServer.VERSION)) {
            writePacket(skidChatServer, client, "error", "Incompatible client version");
            client.disconnect("n");
            return;
        }


        if(!CorrectionUtil.isLegal(username)) {
            writePacket(skidChatServer, client, "error", "Illegal username");
            client.disconnect("n");
            return;
        }

        if(client.getUser() != null) // User is already logged in
            return;

        User user = skidChatServer.getUserManager().loadUserData(username);

        if (user == null) { // User does not exist, create a new one
            user = skidChatServer.getUserManager().createUser(username, hashedPassword);
        }

        for(Client c: skidChatServer.getConnectionHandler().getConnectedClients()) {
            if(Objects.equals(c.getUser(), user)) {
                writePacket(skidChatServer, client, "error", "User already logged in");
                client.disconnect("n");
                return;
            }
        }


        if(user.getHashedPassword().equals(hashedPassword)) {
            client.setUser(user);
            writePacket(skidChatServer, client, "success", skidChatServer.getConfig().get("name"));
            Packets.CHANNEL_LIST.getPacket().writePacket(skidChatServer, client);
            for(Channel channel : skidChatServer.getChannelManager().getChannels()) {
                Packets.CHANNEL_MESSAGE_LIST.getPacket().writePacket(skidChatServer, client, channel.getName());
            }
        }
        else
        {
            writePacket(skidChatServer, client, "error", "Invalid password");
            client.disconnect("n");
        }
    }

    @Override
    public void writePacket(SkidChatServer skidChatServer, Client client, Object... params) throws PacketException {
        if (params.length < 1 || params.length > 2) {
            throw new PacketException("LoginPacket requires 1 or 2 parameters: status and optional message.");
        }

        PacketContext context = new PacketContext(this);
        context.writeString((String) params[0]); // status

        if (params.length == 2) {
            context.writeString((String) params[1]); // optional message
        }

        context.finish();
        skidChatServer.getPacketManager().sendPacket(client, context);
    }
}
