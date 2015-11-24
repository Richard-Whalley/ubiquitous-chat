package fxchat.controllers;

import fxchat.helpers.CurrentUser;
import fxchat.helpers.SpaceUtils;
import fxchat.models.Chatroom;
import fxchat.models.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import net.jini.space.JavaSpace;

public class MainController {

	@FXML private Label chatroomTopic;

    JavaSpace javaSpace;

    public MainController(){
        javaSpace = SpaceUtils.getSpace();
        if (javaSpace == null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Failed to find JavaSpace, please try again.");
            alert.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> System.exit(1));
            System.err.println("Failed to find the javaspace");
        }
    }

    @FXML
    public void createRoom(){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create A Room");
        dialog.setHeaderText("Create a room");
        dialog.setContentText("topic:");

        dialog.showAndWait().ifPresent(topic -> commitRoom(topic));
    }

    private void commitRoom(String topic) {
        Chatroom chatroom = new Chatroom(topic);

        //System.out.println("Owner: " + chatroom.getOwner() + " Topic: " + chatroom.getTopic());
    }

    @FXML
	public void quit(){
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?");
		alert.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> System.exit(1));
    }
}
