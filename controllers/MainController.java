package fxchat.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class MainController {

	@FXML
	public void say(){
		// Do nothing
	}

	@FXML
	public void quit(){
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?");
		alert.showAndWait().filter(response -> response == ButtonType.CLOSE).ifPresent(response -> System.exit(1));
    }
}
