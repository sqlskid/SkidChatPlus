package xyz.sqlskid.skidchat.user;

import lombok.Getter;

@Getter
public class User {

    private String username;
    private String hashedPassword;

    public User(String username, String hashedPassword) {
        this.username = username;
        this.hashedPassword = hashedPassword;
    }


}
