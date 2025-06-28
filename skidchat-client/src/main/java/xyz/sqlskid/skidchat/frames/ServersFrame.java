package xyz.sqlskid.skidchat.frames;

import org.json.JSONObject;
import xyz.sqlskid.skidchat.SkidChatClient;

import javax.swing.*;

public class ServersFrame extends JFrame {

    private SkidChatClient skidChatClient;

    private JComboBox<String> servers;

    public ServersFrame(SkidChatClient skidChatClient){
        this.skidChatClient = skidChatClient;

        setTitle("SkidChat - Servers");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(0, 0, 400, 300);
        add(panel);
        JLabel serverLabel = new JLabel("Servers");
        serverLabel.setBounds(50, 20, 80, 25);
        servers = new JComboBox<>();
        servers.setBounds(150, 20, 200, 25);
        refreshServerList();

        JButton addServerButton = new JButton("Add Server");
        addServerButton.setBounds(50, 60, 300, 25);
        addServerButton.addActionListener(e -> {
            String serverName = JOptionPane.showInputDialog(this, "Enter server name:");
            String serverIp = JOptionPane.showInputDialog(this, "Enter server IP:");
            int serverPort = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter server port:"));
            JSONObject newServer = new JSONObject();
            newServer.put("name", serverName);
            newServer.put("ip", serverIp);
            newServer.put("port", serverPort);
            skidChatClient.getServerList().put(newServer);
            refreshServerList();
            skidChatClient.getLoginFrame().refreshServerList();
        });

        JButton deleteServerButton = new JButton("Delete Server");
        deleteServerButton.setBounds(50, 100, 300, 25);
        deleteServerButton.addActionListener(e -> {
            int selectedIndex = servers.getSelectedIndex();
            if (selectedIndex >= 0) {
                skidChatClient.getServerList().remove(selectedIndex);
                refreshServerList();
                skidChatClient.getLoginFrame().refreshServerList();
            } else {
                JOptionPane.showMessageDialog(this, "No server selected to delete.");
            }
        });

        panel.add(serverLabel);
        panel.add(servers);
        panel.add(addServerButton);
        panel.add(deleteServerButton);

    }

    private void refreshServerList() {
        servers.removeAllItems();
        for(Object obj: skidChatClient.getServerList()) {
            JSONObject server = (JSONObject) obj;
            servers.addItem(server.getString("name"));
        }
    }


}
