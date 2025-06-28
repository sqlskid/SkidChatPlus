package xyz.sqlskid.skidchat.connection;

import lombok.Getter;
import xyz.sqlskid.skidchat.SkidChatClient;
import xyz.sqlskid.skidchat.connection.packet.Packets;
import xyz.sqlskid.skidchat.connection.packet.ext.KeyExchangePacket;
import xyz.sqlskid.skidchat.connection.packet.ext.LoginPacket;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Objects;

public class SkidChatConnection {

    private SkidChatClient skidChatClient;

    @Getter
    private String ip;

    @Getter
    private int port;

    @Getter
    private String username;

    @Getter
    private String password;

    @Getter
    private boolean connected;

    private Socket socket;

    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public SkidChatConnection(SkidChatClient skidChatClient, String ip, int port, String username, String password) {
        this.skidChatClient = skidChatClient;
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
        this.connected = false;
    }

    public void tryConnect() {
        try {
            socket = new Socket(ip, port);
            connected = true;

            Thread readThread = new Thread(this::readThread);
            readThread.setName("SkidChat-ReadThread");
            readThread.start();

            skidChatClient.getEncryptionManager().getRsa().generateKeyPair();
            skidChatClient.getEncryptionManager().getAes().regenerate();
            Packets.KEY_EXCHANGE.getPacket().writePacket(skidChatClient);

        } catch (Exception e) {
            e.printStackTrace();
            skidChatClient.getLoginFrame().setVisible(true);
            skidChatClient.getConnectingFrame().setVisible(false);
            connected = false;
        }
    }

    private void readThread(){
        try {
            inputStream = new DataInputStream(socket.getInputStream());
            while (connected) {
                String message = inputStream.readUTF(); // Assuming the server sends messages as UTF strings
                if (!message.isEmpty()) {
                    skidChatClient.getPacketManager().handle(message);
                }
            }
        } catch (Exception e) {

            skidChatClient.getLoginFrame().setVisible(true);
            skidChatClient.getChatFrame().setVisible(false);
            if(!Objects.equals(e.getMessage(), "Socket closed")) {
                JOptionPane.showMessageDialog(skidChatClient.getLoginFrame(),
                        e.getMessage(),
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            skidChatClient.getKeepAliveWorker().stop();
            skidChatClient.setLoggedIn(false);
            connected = false;
        }
    }

    public void sendData(String rawData) {
        if (!connected) {
            throw new IllegalStateException("Not connected to the server.");
        }
        try {
            outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF(rawData);
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
            connected = false; // Set connected to false if sending fails
        }
    }

    public void disconnect() {
        if (connected) {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
                connected = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
        }
    }
}
