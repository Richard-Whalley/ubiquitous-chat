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

public class RegisterController {

    // FXML based Variables
    @FXML
    private Text statusText;
    @FXML
    protected TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField password_confirm;

    // Controller only variables
    private String errorMsg;

    @FXML
    protected void handleRegisterSubmit(ActionEvent event) {

        if ((password.getText() != null && !password.getText().isEmpty()) || (password_confirm.getText() != null && !password_confirm.getText().isEmpty())) {
            if ((username.getText() != null && !username.getText().isEmpty())) {
                errorMsg = "Username: " + username.getText();
            } else {
                errorMsg = "Username cannot be empty \n";
            }
        } else {
            errorMsg += "Password(s) cannot be empty \n";
        }

        statusText.setText(errorMsg);

    }

    @FXML
    protected void switchContextLogin(ActionEvent event) throws IOException {

        // Get Main stage
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();

        try {
            Parent root = FXMLLoader.load(getClass().getResource("../views/login.fxml"));
            Scene scene = new Scene(root, 320, 240);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
