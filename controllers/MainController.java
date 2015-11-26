package fxchat.controllers;

import fxchat.helpers.SpaceUtils;
import fxchat.models.Chatroom;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import net.jini.space.JavaSpace05;

import java.io.IOException;

public class MainController {

    @FXML
    private Label chatroomTopic;

    JavaSpace05 javaSpace;

    public MainController() {
        javaSpace = SpaceUtils.getSpace();
        if (javaSpace == null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Failed to find JavaSpace05, please try again.");
            alert.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> System.exit(1));
            System.err.println("Failed to find the javaspace");
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
