package xyz.sqlskid.skidchat.util;

import xyz.sqlskid.skidchat.SkidChatServer;

import java.util.List;

public class CorrectionUtil {

    public static boolean isLegal(String input) {
        if (input == null || input.isEmpty()) {
            return false; // Empty or null input is not legal
        }

        // Check for illegal characters
        for (char c : input.toCharArray()) {
            if (!Character.isLetterOrDigit(c) && c != '_' && c != '-') {
                return false; // Illegal character found
            }
        }

        // Check length constraints
        if (input.length() < 3 || input.length() > 20) {
            return false; // Length must be between 3 and 20 characters
        }

        if((boolean) SkidChatServer.getInstance().getConfig().get("whitelist") && !((List<String>) SkidChatServer.getInstance().getConfig().get("whitelisted-usernames")).contains(input)){
            return false;
        }

        return true; // Input is legal
    }

    public static String sanitize(String input) {
        if (input == null) {
            return null; // Return null if input is null
        }

        input = input.replaceAll("\u001F", "");

        return input;
    }

}
