package xyz.sqlskid.skidchat.message;

public class Message {

    private static final String MESSAGE_DELIMITER = "\u001F";


    private String sender;
    private String content;
    private long timestamp;

    public Message(String sender, String content) {
        this.sender = sender;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }

    public Message(String sender, String content, long timestamp) {
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getFormattedTimestamp() {
        // Format the timestamp as needed, e.g., "HH:mm"
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm");
        return sdf.format(new java.util.Date(timestamp));
    }

    public String serialize() {
        return sender + MESSAGE_DELIMITER + content + MESSAGE_DELIMITER + timestamp;
    }

    public static Message deserialize(String data) {
        String[] parts = data.split(MESSAGE_DELIMITER, 3);
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid message format");
        }
        String sender = parts[0];
        String content = parts[1];
        long timestamp = Long.parseLong(parts[2]);
        return new Message(sender, content, timestamp);
    }

    public String getFormattedMessage() {
        if(sender == null || sender.isEmpty()) {
            return content;
        }

        return "[" + getFormattedTimestamp() + "] " + sender + ": " + content;
    }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + sender + ": " + content;
    }

}
