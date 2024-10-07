package com.muxiao.system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class start {
    private final Stage primaryStage=Main.primaryStage;
    @FXML
    public Button login;
    @FXML
    public Button check;
    @FXML
    public void CheckBTNClick(ActionEvent event) throws IOException {
        com.muxiao.system.check.FromPage = "start.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource("check.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
    }
    @FXML
    public void LoginBTNClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
    }
    @FXML
    private void initialize() {}
}
