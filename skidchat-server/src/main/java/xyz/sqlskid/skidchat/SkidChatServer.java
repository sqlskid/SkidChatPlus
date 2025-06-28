package xyz.sqlskid.skidchat;

import lombok.Getter;
import org.yaml.snakeyaml.Yaml;
import xyz.sqlskid.skidchat.channel.Channel;
import xyz.sqlskid.skidchat.channel.ChannelManager;
import xyz.sqlskid.skidchat.client.Client;
import xyz.sqlskid.skidchat.connection.ConnectionHandler;
import xyz.sqlskid.skidchat.connection.packet.PacketException;
import xyz.sqlskid.skidchat.connection.packet.PacketManager;
import xyz.sqlskid.skidchat.connection.packet.Packets;
import xyz.sqlskid.skidchat.encryption.EncryptionManager;
import xyz.sqlskid.skidchat.message.Message;
import xyz.sqlskid.skidchat.user.UserManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class SkidChatServer {

    public static final String VERSION = "1.0.0";

    private List<String> params;

    @Getter
    private HashMap<String, Object> config;

    @Getter
    private static SkidChatServer instance;

    private Yaml configuration;

    @Getter
    private ConnectionHandler connectionHandler;

    @Getter
    private PacketManager packetManager;

    @Getter
    private UserManager userManager;

    @Getter
    private ChannelManager channelManager;

    @Getter
    private EncryptionManager encryptionManager;

    private File configFile;

    public SkidChatServer(List<String> params) {
        this.params = params;
    }

    public void start() {
        instance = this;

        configFile = new File("config.yml");
        config = new HashMap<>();

        if (!configFile.exists()) {
            // Create default config file
            try {
                createDefaultConfig();
            } catch (IOException e) {
                System.err.println("Failed to create default config file: " + e.getMessage());
            }
        }
        // Load the configuration
        try (InputStream inputStream = Files.newInputStream(configFile.toPath())) {
            configuration = new Yaml();
            HashMap<String, Object> configData = configuration.load(inputStream);
            if (configData != null) {
                this.config.putAll(configData);
            }
        } catch (IOException e) {
            System.err.println("Failed to load configuration: " + e.getMessage());
        }

        packetManager = new PacketManager(this);
        userManager = new UserManager(this);
        channelManager = new ChannelManager(this);
        encryptionManager = new EncryptionManager(this);

        connectionHandler = new ConnectionHandler(this);
        connectionHandler.start();

        keepAliveThread();

        System.out.println("SkidChat Server started on port: " + config.get("port"));
        Scanner scanner = new Scanner(System.in);
        System.out.println("Type 'exit' to stop the server.");
        while (true) {
            String input = scanner.nextLine();
            if ("exit".equalsIgnoreCase(input)) {
                stop();
                break;
            }
        }
    }


    public void keepAliveThread(){
        Thread keepAliveThread = new Thread(() -> {
            while (true) {
                List<Client> tempClients = new ArrayList<>(connectionHandler.getConnectedClients());
                for(Client client : tempClients) {
                    try {
                        if (System.currentTimeMillis() - client.getLastKeepAlive() > 60000) { // 60 seconds
                            client.disconnect("Inactive for too long");
                            System.out.println("Disconnected client " + client.getIdentifier() + " due to inactivity.");
                        }
                    }catch (PacketException e) {
                        System.err.println("Error disconnecting client " + client.getIdentifier() + ": " + e.getMessage());
                    }
                }
                try {
                    Thread.sleep(5000);
                }
                catch (Exception e){

                }
            }
        });
        keepAliveThread.setName("SkidChat-KeepAliveThread");
        keepAliveThread.start();
    }

    public void stop() {
        if (channelManager != null && (boolean) config.get("log-channel-messages")) {
            channelManager.saveAll();
        }
        System.exit(1);
    }

    private void createDefaultConfig() throws IOException {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("config.yml")) {
            Files.copy(is, Paths.get("config.yml"));
        } catch (IOException e) {
            throw e;
        }
    }


    public void broadcastMessage(Channel channel, Message message) {
        for (var client : connectionHandler.getConnectedClients()) {
            if(client.getUser() == null) continue;
            try {
                Packets.CHANNEL_MESSAGE.getPacket().writePacket(
                    this, client,
                    channel.getName(),
                    message.getSender(), message.getContent(), message.getTimestamp());
            } catch (PacketException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
