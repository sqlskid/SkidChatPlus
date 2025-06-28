package xyz.sqlskid.skidchat.connection;

import lombok.Getter;
import xyz.sqlskid.skidchat.SkidChatClient;
import xyz.sqlskid.skidchat.connection.packet.Packets;

import java.util.Random;

public class KeepAliveWorker {

    private SkidChatClient client;

    private Thread thread;

    private Random random;

    @Getter
    private int keyExchangeCycle = 0;

    @Getter
    private int aesKeyCycle = 0;

    public KeepAliveWorker(SkidChatClient client) {
        this.client = client;
        this.random = new Random();
    }

    public void start() {
        thread = new Thread(this::run);
        thread.setName("KeepAliveWorker");
        thread.start();
    }

    public void stop() {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
    }

    private void resetKeyExchangeCycle(){
        keyExchangeCycle = random.nextInt(30, 60);
        aesKeyCycle = random.nextInt(5, 15);
    }

    public void run() {
        resetKeyExchangeCycle();
        while (client.getSkidChatConnection().isConnected()){
            try{
                Thread.sleep(random.nextInt(10000, 15000)); // Jitter
                Packets.KEEP_ALIVE.getPacket().writePacket(client);
                if(aesKeyCycle == 0){
                    aesKeyCycle = random.nextInt(5, 15);
                    client.getEncryptionManager().getAes().generateKey();
                }
                if(keyExchangeCycle == 0){
                    keyExchangeCycle = random.nextInt(30, 60);
                    client.forceChangeKey();
                }
                keyExchangeCycle--;
                aesKeyCycle--;
                client.getConnectionInfoFrame().update();
            }catch (Exception e){
            }
        }
    }



}
