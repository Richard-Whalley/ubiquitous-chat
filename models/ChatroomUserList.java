package fxchat.models;

import java.util.ArrayList;

/**
 * Created by rickwhalley on 13/11/2015.
 */
public class ChatroomUserList {
    public Chatroom chatroom;
    public ArrayList users;

    /* Chatroom */
    public Chatroom getChatroom() {
        return chatroom;
    }

    public void setChatroom(Chatroom chatroom) {
        this.chatroom = chatroom;
    }

    /* Users */
    public ArrayList getUsers() {
        return users;
    }

    public void setUsers(ArrayList users) {
        this.users = users;
    }
}
