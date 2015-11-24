package fxchat.models;

import java.util.Calendar;

/**
 * Created by rickwhalley on 24/11/2015.
 */
public class Message {
    String chatTopic;
    String username;
    String message;
    Calendar timestamp;

    public Message(){
        // No Args for Jini
    }

    public Message(String chatTopic, String username, String message){
        chatTopic = this.chatTopic;
        username = this.username;
        message = this.message;
        timestamp = Calendar.getInstance();
    }

    public String getChatTopic() {
        return chatTopic;
    }

    public void setChatTopic(String chatTopic) {
        this.chatTopic = chatTopic;
    }

    public String getUsername() {
        return username;
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
        return timestamp;
    }

    public void setTimestamp(Calendar timestamp) {
        this.timestamp = timestamp;
    }
}
