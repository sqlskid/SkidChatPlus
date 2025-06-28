package xyz.sqlskid.skidchat.channel;

import lombok.Getter;
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
        addMessage(new Message("", "This is the start of the channel " + name + "."));
    }

    public void addMessage(Message message) {
        messageList.add(message);
    }

    public void removeMessage(Message message) {
        messageList.remove(message);
    }

    public List<Message> getMessages() {
        return new ArrayList<>(messageList); // Return a copy to prevent external modification
    }

    public void addMessages(List<Message> messageList) {
        this.messageList.addAll(messageList);
    }
}
