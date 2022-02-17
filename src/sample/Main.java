package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
            primaryStage.setTitle("Smart Wallet");
            primaryStage.setScene(new Scene(root, 1024, 580));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {
        launch(args);
    }


}