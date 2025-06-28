package xyz.sqlskid.skidchat.frames;

import xyz.sqlskid.skidchat.SkidChatClient;

import javax.swing.*;

public class ConnectionInfoFrame extends JFrame {

    private SkidChatClient skidChatClient;

    private JLabel connectionLabel;
    private JLabel clientPublicKeyLabel;
    private JLabel serverPublicKeyLabel;
    private JLabel nextKeyExchangeCycleLabel;
    private JLabel nextAesKeyLabel;

    public ConnectionInfoFrame(SkidChatClient skidChatClient){
        this.skidChatClient = skidChatClient;
        setTitle("SkidChat - Connection Info");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(0, 0, 400, 200);
        add(panel);
        connectionLabel = new JLabel("Connected to: -");
        connectionLabel.setBounds(25, 10, 300, 25);
        panel.add(connectionLabel);

        clientPublicKeyLabel = new JLabel("Client Public Key: -");
        serverPublicKeyLabel = new JLabel("Server Public Key: -");
        clientPublicKeyLabel.setBounds(25, 40, 300, 25);
        serverPublicKeyLabel.setBounds(25, 70, 300, 25);
        panel.add(clientPublicKeyLabel);
        panel.add(serverPublicKeyLabel);

        nextKeyExchangeCycleLabel = new JLabel("Next Key Exchange Cycle: -");
        nextKeyExchangeCycleLabel.setBounds(25, 100, 300, 25);
        panel.add(nextKeyExchangeCycleLabel);
        nextAesKeyLabel = new JLabel("Next AES Key: -");
        nextAesKeyLabel.setBounds(25, 130, 300, 25);
        panel.add(nextAesKeyLabel);
    }

    public void update(){
        clientPublicKeyLabel.setText("Client Public Key: " + skidChatClient.getClientPublicKeyFingerPrint());
        serverPublicKeyLabel.setText("Server Public Key: " + skidChatClient.getServerPublicKeyFingerPrint());

        nextKeyExchangeCycleLabel.setText("Next Key Exchange Cycle: " + skidChatClient.getKeepAliveWorker().getKeyExchangeCycle());
        nextAesKeyLabel.setText("Next AES Key: " + skidChatClient.getKeepAliveWorker().getAesKeyCycle());

        connectionLabel.setText("Connected to: " + skidChatClient.getSkidChatConnection().getIp() + ":" + skidChatClient.getSkidChatConnection().getPort());
    }

}
