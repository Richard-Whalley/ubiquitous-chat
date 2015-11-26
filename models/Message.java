package fxchat.models;

import java.util.Calendar;

import fxchat.helpers.CurrentUser;
import net.jini.core.entry.Entry;


/**
 * Created by rickwhalley on 24/11/2015.
 */
public class Message implements Entry{
    public String chatTopic;
    public String username;
    public String message;
    public Calendar timestamp;

    public Message(){
        // No Args for Jini
    }

    public Message(String chatTopic){
        this.chatTopic = chatTopic;
    }

    public Message(String chatTopic, String username, String message){
        this.chatTopic = chatTopic ;
        this.username = username;
        this.message = message;
        this.timestamp = Calendar.getInstance();
    }

    public String getChatTopic() {
        return this.chatTopic;
    }

    public void setChatTopic(String chatTopic) {
        this.chatTopic = chatTopic;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Calendar getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Calendar timestamp) {
        this.timestamp = timestamp;
    }
}
