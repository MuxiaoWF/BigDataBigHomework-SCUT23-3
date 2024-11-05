package com.muxiao.system;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class loginAdmin extends Application {
    public static Stage stage;
    @Override
    public void start(Stage stage) throws Exception {
        loginAdmin.stage = stage;
        login.isAdmin = true;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = loader.load();
        stage.setTitle("管理员登录");
        stage.setScene(new Scene(root, 600, 400));
        stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        stage.setOnCloseRequest(e -> login.isAdmin = false);
        stage.show();
    }
}
