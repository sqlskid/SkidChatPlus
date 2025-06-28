package xyz.sqlskid.skidchat.client;

import lombok.Getter;
import lombok.Setter;
import xyz.sqlskid.skidchat.SkidChatServer;
import xyz.sqlskid.skidchat.connection.packet.PacketException;
import xyz.sqlskid.skidchat.connection.packet.Packets;
import xyz.sqlskid.skidchat.user.User;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Base64;

public class Client {

    @Getter
    private Socket socket;

    @Getter
    private String identifier;

    @Getter
    @Setter
    private User user;

    @Getter
    @Setter
    private PublicKey publicKey;

    @Getter
    @Setter
    private KeyPair keyPair;

    @Getter
    @Setter
    private SecretKey secretKey;

    @Getter
    @Setter
    private IvParameterSpec iv;

    private SkidChatServer skidChatServer;

    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    @Getter
    @Setter
    private long lastKeepAlive = System.currentTimeMillis();

    private Thread readThread;

    public Client(SkidChatServer skidChatServer, Socket socket){
        this.socket = socket;
        this.skidChatServer = skidChatServer;
        this.identifier = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
    }

    public void start(){
        readThread = new Thread(this::readThread);
        readThread.start();
    }

    public void disconnect(String reason) throws PacketException {
        Packets.DISCONNECT.getPacket().writePacket(skidChatServer, this, reason);
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (Exception e) {
            System.err.println("Error closing socket: " + e.getMessage());
        } finally {
            if (readThread != null && readThread.isAlive()) {
                readThread.interrupt();
            }
            skidChatServer.getConnectionHandler().removeClient(this);
        }
    }

    public void readThread(){
        while (true){
            try {
                try {
                    inputStream = new DataInputStream(socket.getInputStream());
                    outputStream = new DataOutputStream(socket.getOutputStream());
                }
                catch (Exception e){
                    System.err.println("Error initializing streams: " + e.getMessage());
                    return;
                }

                String message = inputStream.readUTF();

                if(!message.isEmpty()){
                    skidChatServer.getPacketManager().handle(this, message);
                }

            } catch (Exception e) {
                try {
                    socket.close();
                }
                catch (Exception e2){

                }
                System.err.println("Error handling packet: " + e.getMessage());
                skidChatServer.getConnectionHandler().removeClient(this);
                break; // Exit the loop if an error occurs
            }
        }
    }

    public void sendData(String rawData) {
        try {
            outputStream.writeUTF(rawData);
            outputStream.flush();
        } catch (Exception e) {
            System.err.println("Error sending data: " + e.getMessage());
        }
    }

    public String getEncodedPublicKey() {
        return keyPair.getPublic() != null ? Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()) : null;
    }

    public String getEncodedPrivateKey() {
        return keyPair.getPrivate() != null ? Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()) : null;
    }

    public String getEncodedSecretKey() {
        return secretKey != null ? Base64.getEncoder().encodeToString(secretKey.getEncoded()) : null;
    }

    public String getEncodedIv() {
        return iv != null ? Base64.getEncoder().encodeToString(iv.getIV()) : null;
    }
}
