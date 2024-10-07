package com.muxiao.system;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class change2 extends Application {
    @Override
    public void start(Stage stage) {
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("修改");
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("change2.fxml")));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML file: " + e.getMessage(), e);
        }
    }

    public void launchPage() {
        // 创建一个新的 Stage 用于显示错误页面
        Stage changeStage = new Stage();
        start(changeStage);
    }
}
