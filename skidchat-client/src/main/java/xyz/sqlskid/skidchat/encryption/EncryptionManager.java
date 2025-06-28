package xyz.sqlskid.skidchat.encryption;

import lombok.Getter;
import xyz.sqlskid.skidchat.SkidChatClient;

public class EncryptionManager {

    private SkidChatClient skidChatClient;

    @Getter
    private AES aes;

    @Getter
    private RSA rsa;

    public EncryptionManager(SkidChatClient skidChatClient){
        this.skidChatClient = skidChatClient;
        this.aes = new AES(skidChatClient);
        this.rsa = new RSA(skidChatClient);
    }




}
