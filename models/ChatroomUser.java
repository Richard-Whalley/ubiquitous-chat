package fxchat.models;

import net.jini.core.entry.Entry;

/**
 * Created by rickwhalley on 13/11/2015.
 */
public class ChatroomUser implements Entry{
    public String chatroom;
    public String user;
    public Boolean isDeleted;


    public ChatroomUser(){
        // No args for Jini
    }

    public ChatroomUser(String chatroom, String user, Boolean deleted) {
        this.chatroom = chatroom;
        this.user = user;
        this.isDeleted = deleted;
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

    public Boolean getDeleted() { return isDeleted; }

    public void setDeleted(Boolean deleted) { isDeleted = deleted; }


}
