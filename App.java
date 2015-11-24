package fxchat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("views/login.fxml"));
        primaryStage.setTitle("Hacker Chat: Login");

        //set Stage boundaries to the upper left corner of the visible bounds of the main screen
        primaryStage.setX(0);
        primaryStage.setY(0);
        primaryStage.setScene(new Scene(root, 320, 240));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
