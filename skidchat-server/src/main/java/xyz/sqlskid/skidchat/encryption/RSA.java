package xyz.sqlskid.skidchat.encryption;

import xyz.sqlskid.skidchat.SkidChatServer;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;

public class RSA {

    private static final String ALGORITHM = "RSA"; // Algorithm for RSA encryption
    private static final int KEY_SIZE = 2048; // Size of the RSA key in bits

    private SkidChatServer skidChatServer;

    public RSA(SkidChatServer skidChatServer){
        this.skidChatServer = skidChatServer;
    }

    public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE);
        var keyPair = keyPairGenerator.generateKeyPair();
        return keyPair;
    }

    public byte[] encrypt(byte[] data, PublicKey publicKey) throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    public byte[] decrypt(byte[] data, PrivateKey privateKey) throws InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException, NoSuchPaddingException,
            NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

}
