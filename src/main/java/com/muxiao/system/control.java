package com.muxiao.system;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class control {
    private static final Stage primaryStage=Main.primaryStage;
    @FXML
    private Button addBTN;
    @FXML
    private Button changeBTN;
    @FXML
    private Button deleteBTN;
    @FXML
    private Button changeStatsBTN;
    @FXML
    private Button backBTN;
    @FXML
    private Button checkBTN;
    @FXML
    public void backBTNClicked() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = loader.load();
        Scene scene = backBTN.getScene();
        scene.setRoot(root);
        primaryStage.setScene(scene);
    }
    @FXML
    private void addBTNClicked() throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("add.fxml"));
        Parent root = loader.load();
        Scene scene = addBTN.getScene();
        scene.setRoot(root);
        primaryStage.setScene(scene);
    }
    @FXML
    private void changeBTNClicked() throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("change.fxml"));
        Parent root = loader.load();
        Scene scene = changeBTN.getScene();
        scene.setRoot(root);
        primaryStage.setScene(scene);
    }
    @FXML
    private void deleteBTNClicked() throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("delete.fxml"));
        Parent root = loader.load();
        Scene scene = deleteBTN.getScene();
        scene.setRoot(root);
        primaryStage.setScene(scene);
    }
    @FXML
    private void changeStatsBTNClicked() throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("changeStats.fxml"));
        Parent root = loader.load();
        Scene scene = changeBTN.getScene();
        scene.setRoot(root);
        primaryStage.setScene(scene);
    }
    @FXML
    private void checkBTNClicked() throws IOException{
        check.FromPage="control.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource("check.fxml"));
        Parent root = loader.load();
        Scene scene = checkBTN.getScene();
        scene.setRoot(root);
        primaryStage.setScene(scene);
    }
}
