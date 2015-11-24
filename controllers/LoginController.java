package fxchat.controllers;

import fxchat.helpers.HashPassword;
import fxchat.helpers.SpaceUtils;
import fxchat.helpers.SwitchContext;
import fxchat.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.jini.space.JavaSpace;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class LoginController {

	@FXML private Text statusText;
	@FXML protected TextField username;
	@FXML private PasswordField password;

	public JavaSpace javaSpace;
	public User retrieved;
	public User template;
    public String hashedInput;


    @FXML
	protected void handleLoginSubmit(ActionEvent event) {

		String errorMsg = "";

		if ((username.getText() != null && !username.getText().isEmpty())) {
            // Attempt to retrieve user from space
			if ((password.getText() != null && !password.getText().isEmpty())) {
                try {
                    //Hash user input for comparison with hashedPassword
                    hashedInput = HashPassword.getInstance().hashPassword(password.getText());
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                try {
                    template = new User(username.getText(), null, false);
                    javaSpace = SpaceUtils.getSpace();
                    if (javaSpace == null) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Failed to find JavaSpace, please try again.");
                        alert.showAndWait().filter(response -> response == ButtonType.CLOSE).ifPresent(response -> System.exit(1));
                        System.err.println("Failed to find the javaspace");
                    }
                    retrieved = (User) javaSpace.readIfExists(template, null, 1000);
                    if (retrieved != null) {
                        String hashedPassword = retrieved.getPassword();

                        /** DEBUG: Password equals checker
                         // System.out.println(hashedPassword + " = " + hashedInput); **/

                        // Check password
                        if (hashedPassword.equals(hashedInput)) {
                            // Render Main Screen
                            Node node = (Node) event.getSource();
                            Stage stage = (Stage) node.getScene().getWindow();

                            SwitchContext main = new SwitchContext("../views/main.fxml", stage, 1280, 800);

                        } else {
                            // Passwords Don't Match
                            errorMsg += "Invalid Password \n";
                        }
                    } else {
                        System.out.println("User not found");
                        errorMsg = "User not Found";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
	protected void switchContextRegister(ActionEvent event) {

		// Get Main stage
		Node node = (Node) event.getSource();
		Stage stage = (Stage) node.getScene().getWindow();

        SwitchContext register = new SwitchContext("../views/register.fxml", stage, 320, 240);

	}
}
