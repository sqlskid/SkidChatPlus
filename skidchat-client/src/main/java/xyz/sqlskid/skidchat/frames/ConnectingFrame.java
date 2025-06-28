package xyz.sqlskid.skidchat.frames;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;

public class ConnectingFrame extends JFrame
{
    public ConnectingFrame() {
        setTitle("SkidChat - Connecting");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);

        JLabel connectingLabel = new JLabel("Connecting to server...");
        connectingLabel.setBounds(50, 50, 200, 25);
        add(connectingLabel);

        setVisible(true);
    }
}
