package xyz.sqlskid.skidchat.encryption;

import xyz.sqlskid.skidchat.SkidChatClient;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class AES {

    private static int IV_SIZE = 16;
    private static int KEY_SIZE = 256;

    private SecretKey secretKey;
    private IvParameterSpec iv;

    private SkidChatClient skidChatClient;

    public AES(SkidChatClient skidChatClient){
        this.skidChatClient = skidChatClient;
    }


    public void regenerate(){
        try {
            generateKey();
            generateIv();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getEncodedKey() {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }
    public String getEncodedIv() {
        return Base64.getEncoder().encodeToString(iv.getIV());
    }

    public void generateIv(){
        byte[] ivBytes = new byte[IV_SIZE];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(ivBytes);
        this.iv = new IvParameterSpec(ivBytes);
    }

    public void generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(KEY_SIZE);
        this.secretKey = keyGen.generateKey();
    }

    public byte[] encrypt(byte[] plaintext) throws GeneralSecurityException {
        generateIv();

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

        return cipher.doFinal(plaintext);
    }

    public byte[] decrypt(SecretKey secretKey, IvParameterSpec iv, byte[] encrypted) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

        return cipher.doFinal(encrypted);
    }

    public String encryptToBase64(String plaintext) throws GeneralSecurityException {
        byte[] encrypted = encrypt(plaintext.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decryptFromBase64(String encodedKey, String encodedIv, String base64Encrypted) throws GeneralSecurityException {
        byte[] encrypted = Base64.getDecoder().decode(base64Encrypted);
        return new String(decrypt(new SecretKeySpec(Base64.getDecoder().decode(encodedKey), "AES"), new IvParameterSpec(Base64.getDecoder().decode(encodedIv)), encrypted));
    }

}
