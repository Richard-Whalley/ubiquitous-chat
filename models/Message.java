package fxchat.models;

import java.util.Calendar;

import com.sun.org.apache.xpath.internal.operations.Bool;
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
    public Boolean isPrivate;

    public Message(){
        // No Args for Jini
    }

    public Message(String chatTopic){
        this.chatTopic = chatTopic;
    }

    public Message(String chatTopic, String username, String message, Boolean isTemplate, Boolean isPrivate){
        this.chatTopic = chatTopic ;
        this.username = username;
        this.message = message;
        if (isTemplate){
            this.timestamp = null;
        } else {
            this.timestamp = Calendar.getInstance();
        }
        this.isPrivate = isPrivate;
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

    public String formatMessage() {
        String date = this.getTimestamp().getTime().toString();
        String user = this.getUsername();
        String message_body = this.getMessage();

        if (this.getIsPrivate()){
            return "[" + date + "][Private] " + user + ": " + message_body;
        } else {
            return "[" + date + "] " + user + ": " + message_body;
        }

        // Construct Message
    }

    public Boolean getIsPrivate() { return this.isPrivate; }
}
