package xyz.sqlskid.skidchat.connection.packet.ext;

import xyz.sqlskid.skidchat.SkidChatServer;
import xyz.sqlskid.skidchat.client.Client;
import xyz.sqlskid.skidchat.connection.packet.Packet;
import xyz.sqlskid.skidchat.connection.packet.PacketContext;
import xyz.sqlskid.skidchat.connection.packet.PacketException;

import java.util.Base64;

public class EncryptedPacket extends Packet {

    public EncryptedPacket() {
        super(901, 3, 3);
    }

    @Override
    public void readPacket(SkidChatServer skidChatServer, Client client, PacketContext context) throws PacketException {
        String encryptedData = context.readString();
        String encryptedKey = context.readString();
        String encryptedIv = context.readString();

        try {
            String decryptedKey = new String(skidChatServer.getEncryptionManager().getRsa().decrypt(Base64.getDecoder().decode(encryptedKey), client.getKeyPair().getPrivate()));
            String decryptedIv = new String(skidChatServer.getEncryptionManager().getRsa().decrypt(Base64.getDecoder().decode(encryptedIv), client.getKeyPair().getPrivate()));
            String decryptedData = skidChatServer.getEncryptionManager().getAes().decryptFromBase64(decryptedKey, decryptedIv, encryptedData);

            if(decryptedData == null || decryptedData.isEmpty()) {
                throw new PacketException("Decrypted data is null or empty");
            }

            skidChatServer.getPacketManager().handle(client, decryptedData);
        } catch (Exception e){
            throw new PacketException("Failed to decrypt packet: " + e.getMessage());
        }
    }

    @Override
    public void writePacket(SkidChatServer skidChatServer, Client client, Object... params) throws PacketException {
        if (params.length < 1 || !(params[0] instanceof String)) {
            throw new PacketException("Invalid parameters for EncryptedPacket");
        }

        client.setIv(skidChatServer.getEncryptionManager().getAes().generateIv());

        String data = (String) params[0];
        try {
            String encryptedData = skidChatServer.getEncryptionManager().getAes().encryptToBase64(data, client.getSecretKey(), client.getIv());
            String encryptedKey = Base64.getEncoder().encodeToString(skidChatServer.getEncryptionManager().getRsa().encrypt(client.getEncodedSecretKey().getBytes(), client.getPublicKey()));
            String encryptedIv = Base64.getEncoder().encodeToString(skidChatServer.getEncryptionManager().getRsa().encrypt(client.getEncodedIv().getBytes(), client.getPublicKey()));

            PacketContext context = new PacketContext(this);
            context.writeString(encryptedData);
            context.writeString(encryptedKey);
            context.writeString(encryptedIv);
            context.finish();

            skidChatServer.getPacketManager().sendPacket(client, context);
        } catch (Exception e) {
            throw new PacketException("Failed to encrypt packet: " + e.getMessage());
        }
    }
}
