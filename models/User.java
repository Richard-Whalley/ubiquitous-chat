package fxchat.models;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;


public class User {
    public String username;
    protected byte[] password;
    protected Boolean isAdmin;

    /* Username */
    public String getUsername() {
        return username;
    }

    public void setUsername(String uname) {
        this.username = uname;
    }

    /* Password */
    public byte[] getPasswordHash() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

}
