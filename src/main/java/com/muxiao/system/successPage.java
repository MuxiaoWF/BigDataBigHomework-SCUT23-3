package com.muxiao.system;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class successPage extends Application {
    private static String successMessage;
    @FXML
    public TextArea successText;
    public Stage stage;

    public static void create(String text) {
        successMessage = text;
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("成功");
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("success.fxml")));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            this.successText = (TextArea) root.lookup("#successText");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML file: " + e.getMessage(), e);
        }
    }

    public void launchSuccessPage() {
        // 创建一个新的 Stage 用于显示错误页面
        Stage successStage = new Stage();
        start(successStage);
        successText.setText(successMessage);
    }
}
