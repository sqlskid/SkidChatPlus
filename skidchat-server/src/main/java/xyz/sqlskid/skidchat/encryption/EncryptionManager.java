package xyz.sqlskid.skidchat.encryption;

import lombok.Getter;
import xyz.sqlskid.skidchat.SkidChatServer;

public class EncryptionManager {

    private SkidChatServer skidChatServer;

    @Getter
    private AES aes;

    @Getter
    private RSA rsa;

    public EncryptionManager(SkidChatServer skidChatServer){
        this.skidChatServer = skidChatServer;
        this.aes = new AES(skidChatServer);
        this.rsa = new RSA(skidChatServer);
    }




}
