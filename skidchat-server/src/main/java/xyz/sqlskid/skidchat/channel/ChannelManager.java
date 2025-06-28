package xyz.sqlskid.skidchat.channel;

import xyz.sqlskid.skidchat.SkidChatServer;
import xyz.sqlskid.skidchat.message.Message;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChannelManager {

    private List<Channel> channels;

    private File channelFolder = new File("channels");

    private SkidChatServer skidChatServer;

    public ChannelManager(SkidChatServer skidChatServer) {
        this.channels = new ArrayList<>();
        this.skidChatServer = skidChatServer;
        if (!channelFolder.exists()) {
            channelFolder.mkdirs(); // Create the directory if it doesn't exist
        }

        for(String s: (List<String>) skidChatServer.getConfig().get("channels")){
            Channel channel = new Channel(s);
            File channelFile = new File(channelFolder, s);
            if (!channelFile.exists()) {
                try {
                    channelFile.createNewFile(); // Create the file if it doesn't exist
                } catch (Exception e) {
                    e.printStackTrace(); // Handle the exception appropriately
                }
            }

            channel.addMessages(loadMessages(channel)); // Load messages from the file


            addChannel(channel);
        }
    }

    public void saveAll(){
        for (Channel channel : channels) {
            saveMessages(channel);
        }
    }

    public List<Message> loadMessages(Channel channel) {
        File channelFile = new File(channelFolder, channel.getName());
        List<Message> messages = new ArrayList<>();

        if (channelFile.exists()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new java.io.FileReader(channelFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    messages.add(Message.deserialize(line)); // Deserialize each line into a Message object
                }
            } catch (Exception e) {
                e.printStackTrace(); // Handle the exception appropriately
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        e.printStackTrace(); // Handle the exception appropriately
                    }
                }
            }
        }
        return messages;
    }

    public void saveMessages(Channel channel) {
        File channelFile = new File(channelFolder, channel.getName());
        try (java.io.FileWriter writer = new java.io.FileWriter(channelFile)) {
            for (Message message : channel.getMessages()) {
                writer.write(message.serialize() + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }

    public void addChannel(Channel channel) {
        channels.add(channel);
    }

    public void removeChannel(Channel channel) {
        channels.remove(channel);
    }

    public List<Channel> getChannels() {
        return new ArrayList<>(channels); // Return a copy to prevent external modification
    }

    public Channel getChannelByName(String name) {
        return channels.stream()
                .filter(channel -> channel.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

}
