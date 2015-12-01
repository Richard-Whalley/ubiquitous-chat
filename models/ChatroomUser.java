package fxchat.models;

import java.util.ArrayList;

/**
 * Created by rickwhalley on 13/11/2015.
 */
public class ChatroomUser {
    public String chatroom;
    public String user;

    public ChatroomUser(){
        // No args for Jini
    }

    public ChatroomUser(String chatroom, String user) {
        this.chatroom = chatroom;
        this.user = user;
    }

    public String getChatroom() {
        return chatroom;
    }

    public void setChatroom(String chatroom) {
        this.chatroom = chatroom;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
