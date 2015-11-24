package fxchat.controllers;

import fxchat.helpers.CurrentUser;
import fxchat.helpers.HashPassword;
import fxchat.helpers.SpaceUtils;
import fxchat.helpers.SwitchContext;
import fxchat.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.jini.space.JavaSpace;

import java.security.NoSuchAlgorithmException;

public class LoginController {

	@FXML private Text statusText;
	@FXML private TextField username;
	@FXML private PasswordField password;

	public JavaSpace javaSpace;

    public LoginController(){
        javaSpace = SpaceUtils.getSpace();
        if (javaSpace == null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Failed to find JavaSpace, please try again.");
            alert.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> System.exit(1));
            System.err.println("Failed to find the javaspace");
        }
    }

    @FXML
	protected void handleLoginSubmit(ActionEvent event) {

        User currentUser;
        User template;
        String hashedInput = null;
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
                    currentUser = (User) javaSpace.readIfExists(template, null, 1000);
                    if (currentUser != null) {
                        String hashedPassword = currentUser.getPassword();

                        /** DEBUG: Password equals checker
                         // System.out.println(hashedPassword + " = " + hashedInput); **/

                        // Set current user object to CurrentUser Singleton
                        CurrentUser.getInstance().setCurrentUser(currentUser);

                        // Check password
                        if (hashedPassword.equals(hashedInput)) {
                            Node node = (Node) event.getSource();
                            Stage stage = (Stage) node.getScene().getWindow();

                            // Render Main Screen
                            SwitchContext main = new SwitchContext("../views/main.fxml", stage, 320, 640);

                        } else {
                            errorMsg += "Invalid Password \n";
                        }
                    } else {
                        errorMsg = "User not Found";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
			} else {
				errorMsg += "Password cannot be empty \n";
			}
		} else {
			errorMsg = "Username cannot be empty \n";
		}
		
		statusText.setText(errorMsg);

	}

	@FXML
	protected void switchContextRegister(ActionEvent event) {
        Node node = (Node) event.getSource();
		Stage stage = (Stage) node.getScene().getWindow();

        SwitchContext register = new SwitchContext("../views/register.fxml", stage, 320, 240);

	}
}
