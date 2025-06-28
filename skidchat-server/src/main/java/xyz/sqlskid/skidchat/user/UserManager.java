package xyz.sqlskid.skidchat.user;

import xyz.sqlskid.skidchat.SkidChatServer;
import xyz.sqlskid.skidchat.util.HashUtil;

import java.io.BufferedReader;
import java.io.File;

public class UserManager {

    private SkidChatServer skidChatServer;

    private File userDataDir;

    public UserManager(SkidChatServer skidChatServer) {
        this.skidChatServer = skidChatServer;
        init();
    }

    public void init() {
        userDataDir = new File("userdata");
        if (!userDataDir.exists()) {
            userDataDir.mkdirs();
        }

    }

    public User loadUserData(String username){
        File userFile = new File(userDataDir, HashUtil.MD5(username));

        if (!userFile.exists()) {
            return null; // User data file does not exist
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new java.io.FileReader(userFile));
            return new User(reader.readLine(), reader.readLine());
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Error reading user data
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public User createUser(String username, String password) {
        User user = new User(username, password);

        File userFile = new File(userDataDir, HashUtil.MD5(username));
        if (userFile.exists()) {
            return null; // User already exists
        }

        try (java.io.FileWriter writer = new java.io.FileWriter(userFile)) {
            writer.write(user.getUsername() + "\n");
            writer.write(user.getHashedPassword() + "\n");
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Error writing user data
        }
    }



}
