package xyz.sqlskid.skidchat.connection.packet.ext;

import xyz.sqlskid.skidchat.SkidChatClient;
import xyz.sqlskid.skidchat.connection.packet.Packet;
import xyz.sqlskid.skidchat.connection.packet.PacketContext;
import xyz.sqlskid.skidchat.connection.packet.PacketException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EncryptedPacket extends Packet {

    public EncryptedPacket() {
        super(901, 3, 3);
    }

    @Override
    public void readPacket(SkidChatClient client, PacketContext context) throws PacketException {
        String encryptedData = context.readString();
        String encryptedKey = context.readString();
        String encryptedIv = context.readString();

        try {
            String decryptedKey = new String(client.getEncryptionManager().getRsa().decrypt(Base64.getDecoder().decode(encryptedKey)));
            String decryptedIv = new String(client.getEncryptionManager().getRsa().decrypt(Base64.getDecoder().decode(encryptedIv)));
            String decryptedData = client.getEncryptionManager().getAes().decryptFromBase64(decryptedKey, decryptedIv, encryptedData);

            if(decryptedData == null || decryptedData.isEmpty()) {
                throw new PacketException("Decrypted data is null or empty");
            }

            // Process the decrypted data
            client.getPacketManager().handle(decryptedData);
        } catch (Exception e){
            throw new PacketException("Failed to decrypt packet: " + e.getMessage());
        }
    }

    @Override
    public void writePacket(SkidChatClient client, Object... params) throws PacketException {
        if (params.length < 1 || !(params[0] instanceof String)) {
            throw new PacketException("Invalid parameters for EncryptedPacket");
        }

        String data = (String) params[0];
        try {
            String encryptedData = client.getEncryptionManager().getAes().encryptToBase64(data);
            String encryptedKey = Base64.getEncoder().encodeToString(client.getEncryptionManager().getRsa().encrypt(client.getEncryptionManager().getAes().getEncodedKey().getBytes(), client.getServerKey()));
            String encryptedIv = Base64.getEncoder().encodeToString(client.getEncryptionManager().getRsa().encrypt(client.getEncryptionManager().getAes().getEncodedIv().getBytes(), client.getServerKey()));


            PacketContext context = new PacketContext(this);
            context.writeString(encryptedData);
            context.writeString(encryptedKey);
            context.writeString(encryptedIv);
            context.finish();

            client.getPacketManager().sendPacket(context);
        } catch (Exception e) {
            throw new PacketException("Failed to encrypt packet: " + e.getMessage());
        }
    }
}
