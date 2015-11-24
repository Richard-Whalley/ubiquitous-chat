package fxchat.helpers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SwitchContext {

    public SwitchContext(String view, Stage stage, int x, int y) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(view));
            Scene scene = new Scene(root, x, y);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
