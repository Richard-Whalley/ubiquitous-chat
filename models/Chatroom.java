package fxchat.models;

import fxchat.helpers.CurrentUser;
import net.jini.core.entry.Entry;

/**
 * Created by rickwhalley on 13/11/2015.
 */
public class Chatroom implements Entry {

    public String topic;
    public String owner;

    public Chatroom() {
        // No args for River
    }

    /** Create a chatroom with the given topic for the current user. **/
    public Chatroom(String topic){
        this.topic = topic;
        this.owner = CurrentUser.getInstance().getUser().getUsername();
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

}
