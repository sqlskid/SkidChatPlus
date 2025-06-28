package xyz.sqlskid.skidchat.frames;

import com.formdev.flatlaf.FlatDarkLaf;
import xyz.sqlskid.skidchat.SkidChatClient;
import xyz.sqlskid.skidchat.channel.Channel;
import xyz.sqlskid.skidchat.channel.ChannelManager;
import xyz.sqlskid.skidchat.connection.packet.PacketException;
import xyz.sqlskid.skidchat.connection.packet.Packets;
import xyz.sqlskid.skidchat.message.Message;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;

public class ChatFrame extends JFrame {

    private SkidChatClient skidChatClient;

    private JList<String> channelList;
    private JTextArea chatArea;
    private Channel selectedChannel;


    public ChatFrame(SkidChatClient skidChatClient) {
        this.skidChatClient = skidChatClient;
        setTitle("SkidChat - Chat");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(true);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem refreshChannelsItem = new JMenuItem("Refresh Channels");
        refreshChannelsItem.addActionListener(e -> {
            refreshChannelList();
        });
        fileMenu.add(refreshChannelsItem);
        JMenuItem disconnectItem = new JMenuItem("Disconnect");
        disconnectItem.addActionListener(e -> {
            skidChatClient.disconnect("n");
            //System.exit(0);
        });
        fileMenu.add(disconnectItem);
        menuBar.add(fileMenu);
        JMenu connectionMenu = new JMenu("Connection");
        JMenuItem connectionInfoItem = new JMenuItem("Connection Info");
        connectionInfoItem.addActionListener(e -> {
            skidChatClient.getConnectionInfoFrame().setVisible(true);
            skidChatClient.getConnectionInfoFrame().update();
        });
        JMenuItem forceKeyExchangeItem = new JMenuItem("Force Key Exchange");
        forceKeyExchangeItem.addActionListener(e -> {
            skidChatClient.forceChangeKey();
        });
        connectionMenu.add(connectionInfoItem);
        connectionMenu.add(forceKeyExchangeItem);
        menuBar.add(connectionMenu);
        setJMenuBar(menuBar);


        channelList = new JList<>();
        channelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        channelList.setLayoutOrientation(JList.VERTICAL);
        channelList.setVisibleRowCount(-1);
        channelList.setListData(new String[]{});
        channelList.setSelectedIndex(0);

        JScrollPane channelScrollPane = new JScrollPane(channelList);
        channelScrollPane.setPreferredSize(new Dimension(200, 600));
        add(channelScrollPane, BorderLayout.WEST);
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setPreferredSize(new Dimension(600, 600));
        add(chatScrollPane, BorderLayout.CENTER);
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        JTextField inputField = new JTextField();
        inputPanel.add(inputField, BorderLayout.CENTER);

        DefaultCaret caret = (DefaultCaret)chatArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        channelList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                refreshChannel();
                selectedChannel = skidChatClient.getChannelManager().getChannelByName(channelList.getSelectedValue());
            }
        });

        inputField.addActionListener(e -> {
            String messageText = inputField.getText();
            if (!messageText.isEmpty()) {
                String selectedChannelName = channelList.getSelectedValue();
                if (selectedChannelName != null) {
                    Channel selectedChannel = skidChatClient.getChannelManager().getChannelByName(selectedChannelName);
                    if (selectedChannel != null) {
                        //selectedChannel.sendMessage(messageText);
                        try {
                            Packets.CHANNEL_MESSAGE.getPacket().writePacket(skidChatClient, selectedChannel.getName(), messageText);
                        } catch (PacketException ex) {
                            throw new RuntimeException(ex);
                        }
                        inputField.setText(""); // Clear the input field after sending
                    } else {
                        chatArea.append("Error: Channel not found.\n");
                    }
                } else {
                    chatArea.append("Error: No channel selected.\n");
                }
            }
        });

        add(inputPanel, BorderLayout.SOUTH);

        pack();
    }

    public void refreshChannel(){
        //refreshChannelList();
        String selectedChannelName = channelList.getSelectedValue();
        if (selectedChannelName != null) {
            Channel selectedChannel = skidChatClient.getChannelManager().getChannelByName(selectedChannelName);
            if (selectedChannel != null) {
                chatArea.setText(""); // Clear the chat area
                selectedChannel.getMessages().forEach(message -> {
                    chatArea.append(message.getFormattedMessage() + "\n");
                });
            } else {
                chatArea.setText("Channel not found: " + selectedChannelName);
            }
        }
    }

    public void appendMessage(Message message, String channelName) {
        if(selectedChannel.getName().equals(channelName)) {
            chatArea.append(message.getFormattedMessage() + "\n");
        }
    }

    public void refreshChannelList() {
        String[] channelNames = skidChatClient.getChannelManager().getChannels().stream()
                .map(Channel::getName)
                .toArray(String[]::new);
        channelList.setListData(channelNames);
        if (channelNames.length > 0) {
            channelList.setSelectedIndex(0); // Select the first channel by default
        }

    }

}
