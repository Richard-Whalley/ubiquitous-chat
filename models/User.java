package fxchat.models;

import net.jini.core.entry.Entry;

public class User implements Entry {
    public String username;
    public String password;
    public Boolean online;

    // NoArgs for River
    public User() {
    }

    public User(String username, String password, boolean online) {
        this.username = username;
        this.password = password;
        this.online = online;
    }

    /* Username */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /* Password */
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    /* Online */
    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }
}
