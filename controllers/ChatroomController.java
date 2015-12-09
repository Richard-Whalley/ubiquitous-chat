package fxchat.controllers;

import fxchat.helpers.CurrentUser;
import fxchat.helpers.SpaceUtils;
import fxchat.models.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
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
import java.util.function.Predicate;

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
    @FXML
    private Button say_button;
    @FXML MenuItem logout;
    @FXML
    Parent root;


    private User currentUser = CurrentUser.getInstance().getUser();
    private String topic;
    private String owner;
    private Boolean isOwner;
    private ObservableList<String> messagelist_array = FXCollections.observableArrayList();
    private ObservableList<String> userlist_array = FXCollections.observableArrayList();
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
        chatroomHeader.setText(topic + " [Moderator: " + owner + "][Chatting as: " + currentUser.getUsername() + "]");

        if (owner.equals(CurrentUser.getInstance().getUser().getUsername())) {
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
        ArrayList<Entry> entry_template_array = new ArrayList<>();
        Message message_owner_template;
        Message message_nonowner_template;
        Message message_template_private_sent;
        ChatroomUser users_template;
        DeleteRoom delete_template;

        if (isOwner) {
            // Owner sees all
            message_owner_template = new Message(this.topic, null, null, true, null);
            entry_template_array.add(message_owner_template);

        } else {
            // User sees all non-private + their own sent private
            message_nonowner_template = new Message(this.topic, null, null, true, false);
            entry_template_array.add(message_nonowner_template);
            message_template_private_sent = new Message(this.topic, CurrentUser.getInstance().getUser().getUsername(), null, true, true);
            entry_template_array.add(message_template_private_sent);
        }

        // DEFAULT
        users_template = new ChatroomUser(topic, null, null);
        entry_template_array.add(users_template);
        delete_template = new DeleteRoom(topic);
        entry_template_array.add(delete_template);


        // Repopulate all message
        try {
            MatchSet allMessages = javaSpace.contents(entry_template_array, null, Lease.FOREVER, Long.MAX_VALUE);
            Entry object;
            // Maybe use instanceOf
            while ((object = allMessages.next()) != null) {
                if (object.getClass().equals(Message.class)) {
                    Message msg = (Message) object;
                    messagelist_array.add(msg.formatMessage());
                    System.out.println("+Message");
                } else if (object.getClass().equals(ChatroomUser.class)) {
                    ChatroomUser chatuser = (ChatroomUser) object;
                    userlist_array.add(chatuser.getUser());
                }
            }
            thread.setItems(messagelist_array);
            userlist.setItems(userlist_array);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Init Security manager
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());

        // Init Exporter
        Exporter myDefaultExporter =
                new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
                        new BasicILFactory(), false, true);

        // Set Remote Listener
        try {
            // Register this as a remote object and get a reference to the 'stub'
            stub = (RemoteEventListener) myDefaultExporter.export(this);
            // Add the listener
            javaSpace.registerForAvailabilityEvent(entry_template_array, null, true, stub, Lease.FOREVER, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void notify(RemoteEvent event) {
        System.out.println("+Notification");
        try {
            AvailabilityEvent e = (AvailabilityEvent) event;
            Entry object = e.getEntry();

            if (object.getClass().equals(Message.class)) {
                Message msg = (Message) object;
                Platform.runLater(() -> messagelist_array.add(msg.formatMessage()));
            } else if (object.getClass().equals(ChatroomUser.class)) {
                ChatroomUser chatuser = (ChatroomUser) object;

                if (chatuser.getDeleted()) {
                    Platform.runLater(() -> userlist_array.removeIf(Predicate.isEqual(chatuser.getUser())));
                    System.out.println("-User:" + chatuser.getUser());
                } else {
                    Platform.runLater(() -> userlist_array.add(chatuser.getUser()));
                    System.out.println("+User:" + chatuser.getUser());
                }

            } else if (object.getClass().equals(DeleteRoom.class)) {
                Platform.runLater(() -> messagelist_array.add("This chatroom has been deleted by: " + owner));
                messageBox.setDisable(true);
                isPrivate.setDisable(true);
                say_button.setDisable(true);
            }
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

    @FXML
    protected void handleLeaveChatroom(ActionEvent event) {
        //TODO: Add transactions

        try {
            ChatroomUser chatroom_currentuser = new ChatroomUser(this.topic, CurrentUser.getInstance().getUser().getUsername(), null);
            ChatroomUser user = (ChatroomUser) javaSpace.take(chatroom_currentuser, null, 1000);
            user.setDeleted(true);
            javaSpace.write(user, null, 10000);

            Stage stage = (Stage) say_button.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @FXML
    protected void handleDeleteChatroom() {

    }

}
