package fxchat.models;

import net.jini.core.entry.Entry;

/**
 * Created by rickwhalley on 07/12/2015.
 */
public class DeleteRoom implements Entry {

    public String topic;

    public DeleteRoom(){
        // No args for Jini
    }

    public DeleteRoom(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
