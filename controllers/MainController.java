package fxchat.controllers;

import fxchat.helpers.CurrentUser;
import fxchat.helpers.SpaceUtils;
import fxchat.models.Chatroom;
import fxchat.models.ChatroomUser;
import fxchat.models.DeleteRoom;
import fxchat.models.Message;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

public class MainController implements RemoteEventListener {

    @FXML
    private ListView<String> roomlist;

    private ObservableList<String> roomlist_array = FXCollections.observableArrayList();
    private RemoteEventListener stub;
    private JavaSpace05 javaSpace;

    public MainController() {
        javaSpace = SpaceUtils.getSpace();
        if (javaSpace == null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Failed to find JavaSpace05, please try again.");
            alert.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> System.exit(1));
            System.err.println("Failed to find the javaspace");
        }
    }

    public void initData(){
        // Grab all messages in chatroom
        try {
            // Init template Array
            Chatroom chatroom_template = new Chatroom(null, null);
            ArrayList<Chatroom> chatroom_template_array = new ArrayList<>();
            chatroom_template_array.add(chatroom_template);

            MatchSet allChatrooms = javaSpace.contents(chatroom_template_array, null, Lease.FOREVER, Long.MAX_VALUE);
            Entry chatroom;
            while ((chatroom = allChatrooms.next()) != null){
                Chatroom ctrm = (Chatroom) chatroom;
                roomlist_array.add(ctrm.getTopic());
            }
            roomlist.setItems(roomlist_array);
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
            // register this as a remote object
            // and get a reference to the 'stub'
            stub = (RemoteEventListener) myDefaultExporter.export(this);
            // add the listener for any chatroom
            ArrayList<Entry> entry_template_array = new ArrayList<>();
            Chatroom chatroom_template = new Chatroom(null, null);
            DeleteRoom delete_template = new DeleteRoom(null);
            entry_template_array.add(chatroom_template);
            entry_template_array.add(delete_template);
            javaSpace.registerForAvailabilityEvent(entry_template_array, null, true, stub, Lease.FOREVER, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void notify(RemoteEvent event){
        try {
            AvailabilityEvent e = (AvailabilityEvent) event;
            Entry object = (Entry) e.getEntry();

            if (object.getClass().equals(Chatroom.class)) {
                // Chatroom added to Space
                Chatroom chatroom = (Chatroom) object;
                Platform.runLater(() -> roomlist_array.add(chatroom.getTopic()));
            } else if (object.getClass().equals(DeleteRoom.class)) {
                // Remove chatroom
                DeleteRoom deleteRoom = (DeleteRoom) object;
                Platform.runLater(() -> roomlist_array.removeIf(Predicate.isEqual(deleteRoom.getTopic())));

            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    public void createRoom() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create A Room");
        dialog.setHeaderText("Create a room");
        dialog.setContentText("topic:");

        dialog.showAndWait().ifPresent(this::commitRoom);
    }

    private void commitRoom(String topic) {
        //TODO: ADD Trans

        try {
            Chatroom chatroom = new Chatroom(topic);
            if (!roomExists(topic)) {
                javaSpace.write(chatroom, null, 1200000);
                ChatroomUser chatUser = new ChatroomUser(topic, CurrentUser.getInstance().getUser().getUsername(), false);
                javaSpace.write(chatUser, null, 1200000);
                loadChatroom(chatroom);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error: Chat Topic Exists");
                alert.setHeaderText("Chat Topic Exists");
                alert.setContentText("Sorry, it looks like there is already a chatroom called " + topic + ". Please try again with a different name.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void joinRoom(ActionEvent event){
        String topic = roomlist.getSelectionModel().getSelectedItem();

        if (null != topic){
            try {
                Chatroom chatroom_template = new Chatroom(topic, null);
                if (roomExists(topic)){
                    Chatroom chatroom = (Chatroom) javaSpace.read(chatroom_template, null, 1200000);
                    ChatroomUser chatUser = new ChatroomUser(topic, CurrentUser.getInstance().getUser().getUsername(), false);
                    javaSpace.write(chatUser, null, 1200000);
                    loadChatroom(chatroom);
                }
            } catch (Exception e){
                e.printStackTrace();
            }

        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "No chatroom selected, please try again.");
            alert.showAndWait().filter(response -> response == ButtonType.OK);
        }
    }

    private boolean roomExists(String topic) {
        boolean bool = false;

        try {
            Chatroom template = new Chatroom(topic, null);
            Chatroom exists = (Chatroom) javaSpace.readIfExists(template, null, 1000);
            bool = null != exists;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bool;
    }

    public Stage loadChatroom(Chatroom chatroom) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "../views/chatroom.fxml"
                )
        );

        Stage stage = new Stage();
        stage.setX(320);
        stage.setY(0);
        stage.setScene(
                new Scene(loader.load())
        );

        ChatroomController controller = loader.<ChatroomController>getController();
        controller.initData(chatroom);
        stage.show();

        stage.setOnCloseRequest(windowEvent -> controller.handleLeaveChatroom(null));
        return stage;
    }

    @FXML
    public void quit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?");
        alert.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> System.exit(1));
    }

    @FXML
    public void handleDeleteRoom(){
        String topic = roomlist.getSelectionModel().getSelectedItem();

        if (null != topic){
            Chatroom chatroom_template = new Chatroom(topic, null);
            try {
                Chatroom chatroom = (Chatroom) javaSpace.readIfExists(chatroom_template, null, Long.MAX_VALUE);
                if (chatroom.getOwner().equals(CurrentUser.getInstance().getUser().getUsername())){
                    System.out.println("Delete Fired");
                    //
                    DeleteRoom delete = new DeleteRoom(topic);
                    javaSpace.write(delete, null, Long.MAX_VALUE);

                    // Perform cleanup
                    ArrayList<Entry> entry_template_array = new ArrayList<>();
                    Message room_messages = new Message(topic);
                    entry_template_array.add(room_messages);
                    ChatroomUser room_users = new ChatroomUser(topic, null, null);
                    entry_template_array.add(room_users);
                    Chatroom room = new Chatroom(topic);
                    entry_template_array.add(room);


                    // Remove Objects from space
                    Collection room_entries = javaSpace.take(entry_template_array, null, 1000, 10000);
                    System.out.println(room_entries.toString());
                    // Null local entries for java garbage collection
                    room_entries = null;

                } else {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You must be the owner of a chatroom in order to delete it.");
                    alert.showAndWait().filter(response -> response == ButtonType.OK);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You must select a chatroom in order to delete it.");
            alert.showAndWait().filter(response -> response == ButtonType.OK);
        }
    }
}
