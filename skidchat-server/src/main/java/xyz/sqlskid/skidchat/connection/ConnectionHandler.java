package xyz.sqlskid.skidchat.connection;

import lombok.Getter;
import xyz.sqlskid.skidchat.SkidChatServer;
import xyz.sqlskid.skidchat.client.Client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ConnectionHandler {

    private SkidChatServer server;

    private Thread connectionHandlerThread;

    @Getter
    private List<Client> connectedClients;

    private ServerSocket serverSocket;

    public ConnectionHandler(SkidChatServer server) {
        this.server = server;
        this.connectedClients = new ArrayList<>();
    }


    public void start() {
        connectionHandlerThread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket((Integer) server.getConfig().get("port"));
            } catch (IOException e) {
                System.err.println("Failed to start server socket: " + e.getMessage());
            }
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    if(connectedClients.size() >= (Integer) server.getConfig().get("max-connections")) {
                        System.out.println("Max clients reached, rejecting connection from: " + clientSocket.getInetAddress().getHostAddress());
                        clientSocket.close();
                        continue;
                    }

                    System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
                    Client client = new Client(server, clientSocket);
                    connectedClients.add(client);
                    client.start();
                } catch (IOException e) {
                    System.err.println("Failed to accept client connection: " + e.getMessage());
                }
            }
        });
        connectionHandlerThread.start();
    }

    public void removeClient(Client client)
    {
        connectedClients.remove(client);
    }
}
