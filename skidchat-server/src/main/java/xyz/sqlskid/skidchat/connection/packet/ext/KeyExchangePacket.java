package xyz.sqlskid.skidchat.connection.packet.ext;

import xyz.sqlskid.skidchat.SkidChatServer;
import xyz.sqlskid.skidchat.client.Client;
import xyz.sqlskid.skidchat.connection.packet.Packet;
import xyz.sqlskid.skidchat.connection.packet.PacketContext;
import xyz.sqlskid.skidchat.connection.packet.PacketException;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyExchangePacket extends Packet {

    public KeyExchangePacket() {
        super(900, 1, 1);
    }

    @Override
    public void readPacket(SkidChatServer skidChatServer, Client client, PacketContext context) throws PacketException {
        String encodedPublicKey = context.readString();
        byte[] publicKeyBytes = Base64.getDecoder().decode(encodedPublicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            keyFactory.generatePublic(keySpec);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            client.setPublicKey(publicKey);
        } catch (Exception e) {
            throw new PacketException("Failed to get RSA KeyFactory: " + e.getMessage());
        }
        System.out.println("Received client's public key");
        writePacket( skidChatServer, client);
    }

    @Override
    public void writePacket(SkidChatServer skidChatServer, Client client, Object... params) throws PacketException {
        try {
            client.setSecretKey(skidChatServer.getEncryptionManager().getAes().generateKey());
            client.setIv(skidChatServer.getEncryptionManager().getAes().generateIv());
            client.setKeyPair(skidChatServer.getEncryptionManager().getRsa().generateKeyPair());
        } catch (Exception e) {
            throw new PacketException("Failed to generate keys: " + e.getMessage());
        }

        PacketContext context = new PacketContext(this);
        context.writeString(client.getEncodedPublicKey());
        context.finish();

        skidChatServer.getPacketManager().sendPacket(client, context);
    }

}
