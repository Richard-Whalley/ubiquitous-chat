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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import jdk.nashorn.internal.ir.annotations.Ignore;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace;

import java.io.IOException;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;

public class RegisterController {

    // FXML based Variables
    @FXML private Text statusText;
    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private PasswordField password_confirm;

    public JavaSpace javaSpace;


    private RegisterController(){
        // Initialize space
        javaSpace = SpaceUtils.getSpace();
        if (javaSpace == null){
            System.err.println("Failed to find the javaspace");
            System.exit(1);
        }
    }

    @FXML
    protected void handleRegisterSubmit(ActionEvent event) {

        // Initialize
        String errorMsg = null;

        if ((username.getText() != null && !username.getText().isEmpty())) {
            if ((password.getText() != null && !password.getText().isEmpty()) || (password_confirm.getText() != null && !password_confirm.getText().isEmpty())) {
                if (password_confirm.getText().equals(password.getText())){
                    try {
                        User usr = new User(username.getText(), HashPassword.getInstance().hashPassword(password.getText()), false);
                        /** DEBUG: Usr object
                        System.out.println("Username: " + usr.getUsername());
                        System.out.println("Password: " + usr.getPassword()); **/
                        javaSpace.write(usr, null, 120000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    errorMsg = "Passwords do not match \n";
                }
            } else {
                errorMsg = "Password(s) cannot be empty \n";
            }
        } else {
            errorMsg = "Username cannot be empty \n";
        }


        statusText.setText(errorMsg);

    }

    @FXML
    protected void switchContextLogin(ActionEvent event){

        // Get Main stage
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();

        SwitchContext login = new SwitchContext("../views/login.fxml", stage, 320, 240);
    }

    private String isValid(){
        // Initialize
        String errorMsg = null;

        if ((username.getText() != null && !username.getText().isEmpty())) {
            if ((password.getText() != null && !password.getText().isEmpty()) || (password_confirm.getText() != null && !password_confirm.getText().isEmpty())) {
                if (password_confirm.getText().equals(password.getText())){
                } else {
                    errorMsg = "Passwords do not match \n";
                }
            } else {
                errorMsg = "Password(s) cannot be empty \n";
            }
        } else {
            errorMsg = "Username cannot be empty \n";
        }
        return errorMsg;
    }
}
