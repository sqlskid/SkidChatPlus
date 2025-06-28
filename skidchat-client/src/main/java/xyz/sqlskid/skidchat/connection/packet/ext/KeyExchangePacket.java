package xyz.sqlskid.skidchat.connection.packet.ext;

import xyz.sqlskid.skidchat.SkidChatClient;
import xyz.sqlskid.skidchat.connection.packet.Packet;
import xyz.sqlskid.skidchat.connection.packet.PacketContext;
import xyz.sqlskid.skidchat.connection.packet.PacketException;
import xyz.sqlskid.skidchat.connection.packet.Packets;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyExchangePacket extends Packet {

    public KeyExchangePacket() {
        super(900, 1, 1);
    }

    @Override
    public void readPacket(SkidChatClient client, PacketContext context) throws PacketException {
        String encodedPublicKey = context.readString();
        byte[] publicKeyBytes = Base64.getDecoder().decode(encodedPublicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            keyFactory.generatePublic(keySpec);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            client.setServerKey(publicKey);
        } catch (Exception e) {
            throw new PacketException("Failed to get RSA KeyFactory: " + e.getMessage());
        }

        if(!client.isLoggedIn()){
            Packets.LOGIN.getPacket().writePacket(client, client.getSkidChatConnection().getUsername(), client.getSkidChatConnection().getPassword());
        }
        client.getConnectionInfoFrame().update();
        System.out.println("Received server's public key");
    }

    @Override
    public void writePacket(SkidChatClient client, Object... params) throws PacketException {
        PacketContext context = new PacketContext(this);
        context.writeString(client.getEncryptionManager().getRsa().getEncodedPublicKey());
        context.finish();

        System.out.println("Exchanging key");
        client.getPacketManager().sendPacket(context);
    }
}
