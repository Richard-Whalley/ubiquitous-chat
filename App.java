package fxchat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("views/login.fxml"));
        primaryStage.setTitle("Hacker Chat: Login");
        primaryStage.setScene(new Scene(root, 320, 240));
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
