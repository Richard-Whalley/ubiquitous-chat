package fxchat.models;

import net.jini.core.entry.Entry;

public class User implements Entry {
    public String username;
    public String password;

    // NoArgs for River
    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
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

}
