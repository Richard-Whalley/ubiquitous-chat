package fxchat.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class LoginController {

	@FXML private Text statusText;
	@FXML protected TextField username;
	@FXML private PasswordField password;

	@FXML
	protected void handleLoginSubmit(ActionEvent event) {

		String errorMsg = "";

		if ((username.getText() != null && !username.getText().isEmpty())) {
			// DEBUG
			// System.out.println("Username: " + username.getText());



			/* 	TODO: 		Check if the username exists in the space
				IF TRUE: 	Proceed
				IF FALSE:	Change Error text
			 */

			// Check Password validity
			if ((password.getText() != null && !password.getText().isEmpty())) {
				byte[] hashedInput = null;
				try {
					//Hash user input for comparison with hashedPassword
					hashedInput = fxchat.helpers.HashPassword.getInstance().hashPassword(password.getText());
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}

				byte[] hashedPassword = null; //TODO: get it from the returned user model

				// Check password
				if (hashedPassword == hashedInput){
					// Render Main Screen
					Node node = (Node) event.getSource();
					Stage stage = (Stage) node.getScene().getWindow();

					try {
						Parent root = FXMLLoader.load(getClass().getResource("../views/main.fxml"));
						Scene scene = new Scene(root, 1280, 800);
						stage.setScene(scene);
						stage.show();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					// Passwords Don't Match
					errorMsg += "Invalid Password \n";
				}
			} else {
				// Passwords Empty
				errorMsg += "Password cannot be empty \n";
			}
		} else {
			// Username Empty
			errorMsg = "Username cannot be empty \n";
		}
		
		statusText.setText(errorMsg);

	}

	@FXML
	protected void switchContextRegister(ActionEvent event) throws IOException {

		// Get Main stage
		Node node = (Node) event.getSource();
		Stage stage = (Stage) node.getScene().getWindow();

		try {
			Parent root = FXMLLoader.load(getClass().getResource("../views/register.fxml"));
			Scene scene = new Scene(root, 320, 240);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
