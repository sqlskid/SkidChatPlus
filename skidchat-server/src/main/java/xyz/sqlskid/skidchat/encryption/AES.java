package xyz.sqlskid.skidchat.encryption;

import xyz.sqlskid.skidchat.SkidChatServer;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

public class AES {

    private static int IV_SIZE = 16;
    private static int KEY_SIZE = 256;

    private SkidChatServer skidChatServer;

    public AES(SkidChatServer skidChatServer){
        this.skidChatServer = skidChatServer;
    }

    public IvParameterSpec generateIv() {
        byte[] ivBytes = new byte[IV_SIZE];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(ivBytes);
        return new IvParameterSpec(ivBytes);
    }

    public SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(KEY_SIZE);
        return keyGen.generateKey();
    }

    public byte[] encrypt(byte[] data, SecretKey secretKey, IvParameterSpec iv) throws Exception {
        var cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        return cipher.doFinal(data);
    }

    public byte[] decrypt(byte[] data, SecretKey secretKey, IvParameterSpec iv) throws Exception {
        var cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        return cipher.doFinal(data);
    }

    public String getEncodedKey(SecretKey secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    public String getEncodedIv(IvParameterSpec iv) {
        return Base64.getEncoder().encodeToString(iv.getIV());
    }

    public String encryptToBase64(String data, SecretKey secretKey, IvParameterSpec iv) throws Exception {
        byte[] encryptedData = encrypt(data.getBytes(), secretKey, iv);
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public String decryptFromBase64(String encryptedData, SecretKey secretKey, IvParameterSpec iv) throws Exception {
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedData = decrypt(decodedData, secretKey, iv);
        return new String(decryptedData);
    }

    public String decryptFromBase64(String encodedKey, String encodedIv, String base64Encrypted) throws Exception {
        byte[] encrypted = Base64.getDecoder().decode(base64Encrypted);
        return new String(decrypt(encrypted, new SecretKeySpec(Base64.getDecoder().decode(encodedKey), "AES"), new IvParameterSpec(Base64.getDecoder().decode(encodedIv))));
    }



}
