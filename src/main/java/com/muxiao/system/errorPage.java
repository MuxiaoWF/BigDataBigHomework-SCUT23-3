package com.muxiao.system;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class errorPage extends Application {

    @FXML
    public Button copyBTN;
    @FXML
    public TextArea errorText;

    public Stage stage;
    private static String errorMessage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("错误信息");
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("error.fxml")));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            this.errorText = (TextArea) root.lookup("#errorText");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML file: " + e.getMessage(), e);
        }
    }

    public static void create(String text) {
        errorMessage = text;
    }
    public void launchErrorPage() {
        // 创建一个新的 Stage 用于显示错误页面
        Stage errorStage = new Stage();
        start(errorStage);
        errorText.setText(errorMessage);
    }
    @FXML
    private void copyBTNClick() {
        errorText.selectAll();
        errorText.copy();
    }
}
