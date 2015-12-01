package fxchat.controllers;

import fxchat.helpers.CurrentUser;
import fxchat.helpers.SpaceUtils;
import fxchat.models.Chatroom;
import fxchat.models.Message;
import fxchat.models.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import net.jini.core.entry.Entry;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.space.AvailabilityEvent;
import net.jini.space.JavaSpace05;
import net.jini.space.MatchSet;

import java.util.ArrayList;

/**
 * Created by rickwhalley on 24/11/2015.
 */
public class ChatroomController implements RemoteEventListener {

    @FXML
    private TextField messageBox;
    @FXML
    private Label chatroomHeader;
    @FXML
    private ListView<String> thread;
    @FXML
    private ListView<String> userlist;
    @FXML
    private CheckBox isPrivate;


    private User currentUser = CurrentUser.getInstance().getUser();
    private String topic;
    private String owner;
    private Boolean isOwner;
    private ObservableList<String> messagethread = FXCollections.observableArrayList();
    private JavaSpace05 javaSpace;
    private RemoteEventListener stub;


    public ChatroomController() {

        // Create a space
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

        if (owner.equals(CurrentUser.getInstance().getUser().getUsername())){
            this.isOwner = true;
            isPrivate.setVisible(false);
        } else {
            this.isOwner = false;
        }

        // Listview Cell Factory Lambda to wrap text
        thread.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    TextFlow tf = new TextFlow(new Text(item));
                    tf.maxWidthProperty().bind(lv.widthProperty());
                    setGraphic(tf);
                }
            }
        });

        // Templating
        ArrayList<Message> message_template_array = new ArrayList<>();
        Message message_owner_template;
        Message message_nonowner_template;
        Message message_template_private_sent;

        if (isOwner) {
            // Owner sees all
            message_owner_template = new Message(this.topic, null, null, true, null);
            message_template_array.add(message_owner_template);
        } else {
            // User sees all non-private + their own sent private
            message_nonowner_template = new Message(this.topic, null, null, true, false);
            message_template_array.add(message_nonowner_template);
            message_template_private_sent = new Message(this.topic, CurrentUser.getInstance().getUser().getUsername(), null, true, true);
            message_template_array.add(message_template_private_sent);
        }

        // Repopulate all message
        try {
            MatchSet allMessages = javaSpace.contents(message_template_array, null, Lease.FOREVER, Long.MAX_VALUE);
            Entry message;
            while ((message = allMessages.next()) != null) {
                Message msg = (Message) message;
                messagethread.add(msg.formatMessage());
            }
            thread.setItems(messagethread);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set up the security manager
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());

        // Create the exporter
        Exporter myDefaultExporter =
                new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
                        new BasicILFactory(), false, true);

        // Set Remote Listener
        try {
            // Register this as a remote object and get a reference to the 'stub'
            stub = (RemoteEventListener) myDefaultExporter.export(this);
            // Add the listener
            javaSpace.registerForAvailabilityEvent(message_template_array, null, true, stub, Lease.FOREVER, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void notify(RemoteEvent event) {
        try {
            AvailabilityEvent e = (AvailabilityEvent) event;
            Entry messageEntry = e.getEntry();
            Message message = (Message) messageEntry;
            Platform.runLater(() -> messagethread.add(message.formatMessage()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void say(ActionEvent event) {
        try {
            Message message = new Message(this.topic, currentUser.getUsername(), messageBox.getText(), false, isPrivate.isSelected());
            javaSpace.write(message, null, 120000);
            messageBox.setText(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


//    @FXML
//    protected void repaintThreadAll() {
//        try {
//            thread.getItems().clear();
//            Message message_template = new Message(this.topic);
//            ArrayList<Message> message_template_array = new ArrayList<>();
//            message_template_array.add(message_template);
//            MatchSet allMessages = javaSpace.contents(message_template_array, null, Lease.FOREVER, Long.MAX_VALUE);
//            Entry message;
//            while ((message = allMessages.next()) != null) {
//                Message msg = (Message) message;
//                messagethread.add(msg.formatMessage());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        thread.setItems(messagethread);
//    }

    @FXML
    protected void handleLeaveChatroom() {
        //TODO: Stub
    }

    @FXML
    protected void handleDeleteChatroom() {
        //TODO: Stub
    }

}
