package xyz.sqlskid.skidchat.encryption;

import lombok.Getter;
import xyz.sqlskid.skidchat.SkidChatClient;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.util.Base64;

public class RSA {

    private static final String ALGORITHM = "RSA"; // Algorithm for RSA encryption
    private static final int KEY_SIZE = 2048; // Size of the RSA key in bits

    private SkidChatClient skidChatClient;

    @Getter
    private PublicKey publicKey;

    @Getter
    private PrivateKey privateKey;

    public RSA(SkidChatClient skidChatClient) {
        this.skidChatClient = skidChatClient;
    }

    public String getEncodedPublicKey() {
        return publicKey != null ? Base64.getEncoder().encodeToString(publicKey.getEncoded()) : null;
    }

    public String getEncodedPrivateKey() {
        return privateKey != null ? Base64.getEncoder().encodeToString(privateKey.getEncoded()) : null;
    }

    public void generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE);
        var keyPair = keyPairGenerator.generateKeyPair();
        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
    }

    public byte[] encrypt(byte[] data, PublicKey publicKey) throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    public byte[] decrypt(byte[] data) throws InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException, NoSuchPaddingException,
            NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }




}
