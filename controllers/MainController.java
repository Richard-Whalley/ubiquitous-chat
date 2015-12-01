package fxchat.controllers;

import fxchat.helpers.SpaceUtils;
import fxchat.models.Chatroom;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import java.rmi.Remote;
import java.util.ArrayList;

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
            Chatroom chatroom_template = new Chatroom(null);
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
            // add the listener
            Chatroom chatroom_template = new Chatroom(null);
            ArrayList<Chatroom> chatroom_template_array = new ArrayList<>();
            chatroom_template_array.add(chatroom_template);
            javaSpace.registerForAvailabilityEvent(chatroom_template_array, null, true, stub, Lease.FOREVER, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void notify(RemoteEvent event){
        try {
            AvailabilityEvent e = (AvailabilityEvent) event;
            Entry chatroomEntry = (Entry) e.getEntry();
            Chatroom chatroom = (Chatroom) chatroomEntry;
            Platform.runLater(() -> roomlist_array.add(chatroom.getTopic()));
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

    @FXML
    public void joinRoom(){
        //TODO: join room code
    }

    private void commitRoom(String topic) {
        try {
            Chatroom chatroom = new Chatroom(topic);
            if (!roomExists(topic)) {
                javaSpace.write(chatroom, null, 120000);
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

    private boolean roomExists(String topic) {
        boolean bool = false;

        try {
            Chatroom template = new Chatroom(topic);
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
        return stage;
    }

    @FXML
    public void quit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?");
        alert.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> System.exit(1));
    }
}
