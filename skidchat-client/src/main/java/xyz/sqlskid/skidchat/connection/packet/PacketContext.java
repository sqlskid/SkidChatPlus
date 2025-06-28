package xyz.sqlskid.skidchat.connection.packet;

import lombok.Getter;

import java.util.List;

public class PacketContext {

    private static final String LIST_DELIMITER = "\u001F";

    @Getter
    private int packetId;
    @Getter
    private String rawData;
    @Getter
    private int index = 1;
    private String[] args;

    public PacketContext(String rawData) throws PacketException {
        this.rawData = rawData;

        args = rawData.split(Packet.PACKET_DELIMITER);
        if (args.length > 0) {
            try {
                packetId = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                packetId = -1; // Invalid packet ID
                throw new PacketException("Invalid packet ID: " + args[0]);
            }
        } else {
            packetId = -1; // No data provided
            throw new PacketException("Packet data is empty or malformed.");
        }
    }

    public PacketContext(Packet packet){
        this.rawData = packet.getId() + Packet.PACKET_DELIMITER;
        this.packetId = packet.getId();
        this.args = new String[packet.getWriteParamCount()];
        this.index = 0; // Start reading from the first argument
    }

    public void writeString(String value) {
        if (index >= args.length) {
            throw new ArrayIndexOutOfBoundsException("No more space to write data.");
        }
        args[index++] = value;
    }

    public void writeList(List<String> value) {
        if (index >= args.length) {
            throw new ArrayIndexOutOfBoundsException("No more space to write data.");
        }
        // Join the list into a single string with commas
        args[index++] = String.join(LIST_DELIMITER, value);
    }

    public void writeInt(int value) {
        if (index >= args.length) {
            throw new ArrayIndexOutOfBoundsException("No more space to write data.");
        }
        args[index++] = String.valueOf(value);
    }

    public void writeBoolean(boolean value) {
        if (index >= args.length) {
            throw new ArrayIndexOutOfBoundsException("No more space to write data.");
        }
        args[index++] = value ? "true" : "false";
    }

    public void writeLong(long value) {
        if (index >= args.length) {
            throw new ArrayIndexOutOfBoundsException("No more space to write data.");
        }
        args[index++] = String.valueOf(value);
    }

    public void writeDouble(double value) {
        if (index >= args.length) {
            throw new ArrayIndexOutOfBoundsException("No more space to write data.");
        }
        args[index++] = String.valueOf(value);
    }

    public void finish() {
        // Join the arguments into a single string with the packet delimiter
        rawData = packetId + Packet.PACKET_DELIMITER + String.join(Packet.PACKET_DELIMITER, args);
    }

    public String readString() throws PacketException {
        if (index >= args.length) {
            throw new PacketException("No more data to read.");
        }
        return args[index++];
    }

    public List<String> readList() throws PacketException {
        if (index >= args.length) {
            throw new PacketException("No more data to read.");
        }
        String value = args[index++];
        if (value.isEmpty()) {
            return List.of(); // Return an empty list if the value is empty
        }
        return List.of(value.split(LIST_DELIMITER));
    }

    public int readInt() throws PacketException {
        if (index >= args.length) {
            throw new PacketException("No more data to read.");
        }
        try {
            return Integer.parseInt(args[index++]);
        } catch (NumberFormatException e) {
            throw new PacketException("Invalid integer value: " + args[index - 1]);
        }
    }

    public boolean readBoolean() throws PacketException {
        if (index >= args.length) {
            throw new PacketException("No more data to read.");
        }
        String value = args[index++];
        if ("true".equalsIgnoreCase(value) || "1".equals(value)) {
            return true;
        } else if ("false".equalsIgnoreCase(value) || "0".equals(value)) {
            return false;
        } else {
            throw new PacketException("Invalid boolean value: " + value);
        }
    }

    public long readLong() throws PacketException {
        if (index >= args.length) {
            throw new PacketException("No more data to read.");
        }
        try {
            return Long.parseLong(args[index++]);
        } catch (NumberFormatException e) {
            throw new PacketException("Invalid long value: " + args[index - 1]);
        }
    }

    public double readDouble() throws PacketException {
        if (index >= args.length) {
            throw new PacketException("No more data to read.");
        }
        try {
            return Double.parseDouble(args[index++]);
        } catch (NumberFormatException e) {
            throw new PacketException("Invalid double value: " + args[index - 1]);
        }
    }



}
