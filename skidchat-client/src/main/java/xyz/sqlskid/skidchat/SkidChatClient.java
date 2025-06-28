package xyz.sqlskid.skidchat;

import com.formdev.flatlaf.FlatDarkLaf;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;
import xyz.sqlskid.skidchat.channel.ChannelManager;
import xyz.sqlskid.skidchat.connection.KeepAliveWorker;
import xyz.sqlskid.skidchat.connection.SkidChatConnection;
import xyz.sqlskid.skidchat.connection.packet.PacketManager;
import xyz.sqlskid.skidchat.connection.packet.Packets;
import xyz.sqlskid.skidchat.encryption.EncryptionManager;
import xyz.sqlskid.skidchat.frames.*;
import xyz.sqlskid.skidchat.util.HashUtil;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.PublicKey;

public class SkidChatClient {

    public static final String VERSION = "1.0.0";

    private File skidChatRootDir;
    private File skidChatConfigFile;
    private File serverFile;

    @Getter
    private JSONObject skidChatConfig;

    @Getter
    private JSONArray serverList;

    @Getter
    private static SkidChatClient instance;

    @Getter
    private LoginFrame loginFrame;

    @Getter
    private ChatFrame chatFrame;

    @Getter
    private ConnectingFrame connectingFrame;

    @Getter
    private ConnectionInfoFrame connectionInfoFrame;

    @Getter
    private PacketManager packetManager;

    @Getter
    private SkidChatConnection skidChatConnection;

    @Getter
    private ChannelManager channelManager;

    @Getter
    private KeepAliveWorker keepAliveWorker;

    @Getter
    private EncryptionManager encryptionManager;

    @Getter
    private ServersFrame serversFrame;

    @Setter
    @Getter
    private PublicKey serverKey;

    @Getter
    @Setter
    private boolean loggedIn = false;

    public void start(){
        instance = this;
        skidChatRootDir = new File(System.getProperty("user.home"), ".skidchat");
        skidChatConfigFile = new File(skidChatRootDir, "config.json");
        serverFile = new File(skidChatRootDir, "server.json");

        if(!skidChatRootDir.exists()) {
            skidChatRootDir.mkdirs();
        }

        if(!skidChatConfigFile.exists()) {
            try {
                skidChatConfigFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(!serverFile.exists()) {
            try {
                serverFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        reloadConfig();

        packetManager = new PacketManager(this);
        channelManager = new ChannelManager(this);
        keepAliveWorker = new KeepAliveWorker(this);
        encryptionManager = new EncryptionManager(this);

        Runtime.getRuntime().addShutdownHook(new Thread(this::saveEverything));

        FlatDarkLaf.setup();

        loginFrame = new LoginFrame(this);
        serversFrame = new ServersFrame(this);
        connectionInfoFrame = new ConnectionInfoFrame(this);
    }

    public String getServerPublicKeyFingerPrint(){
        return HashUtil.SHA256(serverKey.getEncoded()).substring(0, 16).replaceAll("(.{2})", "$1 ");
    }

    public String getClientPublicKeyFingerPrint(){
        return HashUtil.SHA256(encryptionManager.getRsa().getPublicKey().getEncoded()).substring(0, 16).replaceAll("(.{2})", "$1 ");
    }

    public void forceChangeKey(){
        try{
            encryptionManager.getRsa().generateKeyPair();
            Packets.KEY_EXCHANGE.getPacket().writePacket(this);
            connectionInfoFrame.update();
        }
        catch (Exception e) {
            System.err.println("Failed to regenerate RSA keys: " + e.getMessage());
        }
    }

    public void saveEverything(){
        FileWriter fileWriter = null;

        skidChatConfig.put("username", loginFrame.getUsername());
        skidChatConfig.put("password", loginFrame.getPassword());

        try{
            fileWriter = new FileWriter(skidChatConfigFile);
            fileWriter.write(skidChatConfig.toString(4));
            fileWriter.close();

            fileWriter = new FileWriter(serverFile);
            fileWriter.write(serverList.toString(4));
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        try {
            skidChatConfig = new JSONObject(new String(Files.readAllBytes(skidChatConfigFile.toPath())));
        } catch (Exception e) {
            skidChatConfig = new JSONObject();
            skidChatConfig.put("username", "");
            skidChatConfig.put("password", "");
            skidChatConfig.put("server", 0);
        }

        try{
            serverList = new JSONArray(new String(Files.readAllBytes(serverFile.toPath())));
        }
        catch (Exception e) {
            serverList = new JSONArray();
            JSONObject defaultServer = new JSONObject();
            defaultServer.put("name", "Localhost");
            defaultServer.put("ip", "127.0.0.1");
            defaultServer.put("port", 3386);
            serverList.put(defaultServer);
        }
    }

    public void connectToServer(String ip, int port, String username, String password){
        connectingFrame = new ConnectingFrame();
        chatFrame = new ChatFrame(this);
        loginFrame.setVisible(false);
        skidChatConnection = new SkidChatConnection(this, ip, port, username, password);
        skidChatConnection.tryConnect();

        if(skidChatConnection.isConnected()) {
            connectingFrame.setVisible(false);
        } else {
            loginFrame.setVisible(true);
        }

    }


    public void disconnect(String reason) {
        loggedIn = false;
        skidChatConnection.disconnect();

        if(!reason.equals("n"))
            JOptionPane.showMessageDialog(null, "Disconnected from server: " + reason, "Disconnected", JOptionPane.ERROR_MESSAGE);
    }
}
