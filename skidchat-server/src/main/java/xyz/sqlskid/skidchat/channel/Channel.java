package xyz.sqlskid.skidchat.channel;

import lombok.Getter;
import xyz.sqlskid.skidchat.SkidChatServer;
import xyz.sqlskid.skidchat.client.Client;
import xyz.sqlskid.skidchat.message.Message;

import java.util.ArrayList;
import java.util.List;

public class Channel {

    @Getter
    private String name;

    private List<Message> messageList;

    public Channel(String name) {
        this.name = name;
        this.messageList = new ArrayList<>();
    }

    public void addMessage(Message message) {
        messageList.add(message);

        SkidChatServer.getInstance().broadcastMessage(this, message);
    }

    public void removeMessage(Message message) {
        messageList.remove(message);
    }

    public void addMessages(List<Message> messages) {
        messageList.addAll(messages); // Add multiple messages at once
    }

    public List<Message> getMessages() {
        return new ArrayList<>(messageList); // Return a copy to prevent external modification
    }
}
