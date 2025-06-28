package xyz.sqlskid.skidchat.frames;

import com.formdev.flatlaf.FlatDarkLaf;
import org.json.JSONObject;
import xyz.sqlskid.skidchat.SkidChatClient;
import xyz.sqlskid.skidchat.util.HashUtil;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginFrame extends JFrame {

    private SkidChatClient client;
    private JComboBox<String> servers;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private boolean hashed = false;

    public LoginFrame(SkidChatClient skidChatClient) {
        setTitle("SkidChat - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);

        this.client = skidChatClient;

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem manageServers = new JMenuItem("Manage Servers");
        manageServers.addActionListener(e -> {
            client.getServersFrame().setVisible(true);
        });
        fileMenu.add(manageServers);
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> {
            System.exit(0);
        });
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);


        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(0, 0, 400, 300);
        add(panel);

        JLabel serverLabel = new JLabel("Server");
        serverLabel.setBounds(50, 20, 80, 25);
        servers = new JComboBox<>();
        servers.setBounds(150, 20, 200, 25);
        refreshServerList();
        servers.setSelectedIndex(client.getSkidChatConfig().getInt("server"));
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setBounds(50, 50, 80, 25);
        usernameField = new JTextField(client.getSkidChatConfig().getString("username"));
        usernameField.setBounds(150, 50, 200, 25);
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(50, 100, 80, 25);
        passwordField = new JPasswordField(client.getSkidChatConfig().getString("password"));
        passwordField.setBounds(150, 100, 200, 25);
        hashed = true;

        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt) {
                hashed = false;
            }
        });
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(150, 150, 100, 25);

        loginButton.addActionListener(e -> {
            JSONObject selectedServer = (JSONObject) client.getServerList().get(servers.getSelectedIndex());
            String password = hashed ? new String(passwordField.getPassword()) : HashUtil.MD5(new String(passwordField.getPassword()) + selectedServer.getString("ip"));

            client.connectToServer(selectedServer.getString("ip"), selectedServer.getInt("port"), usernameField.getText(),
                                   password);
        });

        panel.add(serverLabel);
        panel.add(servers);
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);

        setVisible(true);
    }

    void refreshServerList() {
        servers.removeAllItems();
        for(Object obj: client.getServerList()) {
            JSONObject server = (JSONObject) obj;
            servers.addItem(server.getString("name"));
        }
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        JSONObject selectedServer = (JSONObject) client.getServerList().get(servers.getSelectedIndex());
        return HashUtil.MD5(new String(new String(passwordField.getPassword()) + selectedServer.getString("ip")));
    }
}
