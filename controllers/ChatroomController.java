package fxchat.controllers;

import fxchat.helpers.CurrentUser;
import fxchat.helpers.SpaceUtils;
import fxchat.models.Chatroom;
import fxchat.models.Message;
import fxchat.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace05;
import net.jini.space.MatchSet;
import net.jini.core.entry.Entry;


import java.util.ArrayList;

/**
 * Created by rickwhalley on 24/11/2015.
 */
public class ChatroomController {

    @FXML
    private TextField messageBox;
    @FXML
    private Label chatroomHeader;
    @FXML
    private ListView<String> thread;
    @FXML
    private ListView<String> userlist;


    private User currentUser = CurrentUser.getInstance().getUser();
    private String topic;
    private String owner;

    JavaSpace05 javaSpace;

    public ChatroomController() {
        javaSpace = SpaceUtils.getSpace();
        if (javaSpace == null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Failed to find JavaSpace, please try again.");
            alert.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> System.exit(1));
            System.err.println("Failed to find the javaspace");
        }
    }

    public void initData(Chatroom chatroom) {
        topic = chatroom.getTopic();
        owner = chatroom.getOwner();
        chatroomHeader.setText(topic + " [moderator: " + owner + "]");
        ObservableList<String> messagethread = FXCollections.observableArrayList();
        // TODO: Grab all messages for this chatroom and populate thread
        thread.setItems(messagethread);
    }

    @FXML
    public void say(ActionEvent event) {

        System.out.println(messageBox.getText());

        try {
            Message message = new Message(this.topic, currentUser.getUsername(), messageBox.getText());
            javaSpace.write(message, null, 120000);

            // Testing
            Message template = new Message(this.topic);
            Message retrievedMessage = (Message) javaSpace.readIfExists(template, null, 12000);
            System.out.println("Message: " + retrievedMessage.getMessage());


            /** Test space access for giggles **/
//            Chatroom template = new Chatroom(topic);
//            Chatroom exists = (Chatroom) javaSpace.readIfExists(template, null, 1000);
//
//            System.out.println("Chatroom owner: " + exists.getOwner());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    protected void repaintThread() {

        try {
            Message template = new Message(this.topic);
            ArrayList<Message> tmpls = new ArrayList<Message>();
            tmpls.add(template);
            MatchSet allMessages = javaSpace.contents(tmpls, null, Lease.FOREVER, Long.MAX_VALUE);
            Entry message;
            while ((message=allMessages.next()) != null){
                Message msg = (Message) message;
                System.out.println("Message: " + msg.getMessage());
            }
            /** DEBUG: Fallback single read**/
//            Message retrievedMessage = (Message) javaSpace.readIfExists(template, null, 12000);
//            System.out.println("Message: " + retrievedMessage.getMessage());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void formatMessage() {

    }


}
