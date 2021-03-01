package System;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("fruit.fxml"));
        primaryStage.setTitle("Fruit Analyser");
        TextArea textArea = new TextArea();
        primaryStage.setScene(new Scene(root, 1017, 639));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
