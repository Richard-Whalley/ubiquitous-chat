package fxchat.controllers;

import fxchat.helpers.HashPassword;
import fxchat.helpers.SpaceUtils;
import fxchat.helpers.SwitchContext;
import fxchat.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.jini.space.JavaSpace05;

public class RegisterController {

    // FXML based Variables
    @FXML
    private Text statusText;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField password_confirm;

    public JavaSpace05 javaSpace;


    public RegisterController() {
        // Initialize space
        javaSpace = SpaceUtils.getSpace();
        if (javaSpace == null) {
            System.err.println("Failed to find the javaspace");
            System.exit(1);
        }
    }

    @FXML
    protected void handleRegisterSubmit(ActionEvent event) {

        // Initialize
        String validation = this.formValid();

        if (validation == null) {
            try {
                User usr = new User(username.getText(), HashPassword.getInstance().hashPassword(password.getText()), false);
                /** DEBUG: Usr object
                 System.out.println("Username: " + usr.getUsername());
                 System.out.println("Password: " + usr.getPassword()); **/

                if (!userExists()) {
                    // Write user to space perma
                    javaSpace.write(usr, null, Long.MAX_VALUE);
                    statusText.setText("User " + usr.getUsername() + " Created; Please login.");
                } else {
                    statusText.setText("Username already exists");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            statusText.setText(validation);
        }


    }

    @FXML
    protected void switchContextLogin(ActionEvent event) {

        // Get Main stage
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();

        SwitchContext login = new SwitchContext("../views/login.fxml", stage, 320, 240);
    }

    private String formValid() {
        // Initialize
        String errorMsg = null;

        if ((username.getText() == null && username.getText().isEmpty())) {
            errorMsg = "Username cannot be empty \n";
        } else {
            if ((password.getText() != null && !password.getText().isEmpty()) || (password_confirm.getText() != null && !password_confirm.getText().isEmpty())) {
                if (!password_confirm.getText().equals(password.getText())) {
                    errorMsg = "Passwords do not match \n";
                }
            } else {
                errorMsg = "Password(s) cannot be empty \n";
            }
        }

        return errorMsg;
    }

    private boolean userExists() {

        boolean bool = false;

        try {
            User template = new User(username.getText(), null, false);
            User currentUser = (User) javaSpace.readIfExists(template, null, 1000);
            if (null != currentUser) bool = true;
            else bool = false;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bool;

    }
}
