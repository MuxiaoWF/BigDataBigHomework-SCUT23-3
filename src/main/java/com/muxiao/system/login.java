package com.muxiao.system;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;

public class login {
    private final Stage primaryStage = Main.primaryStage;
    @FXML
    public Button okBTN;
    @FXML
    public PasswordField passwordText;
    @FXML
    public TextField userText;
    @FXML
    private Button backBTN;
    @FXML
    private Button registerBTN;

    @FXML
    public void registerBTNClick() {
        if (!userText.getText().isEmpty() && !passwordText.getText().isEmpty()) {
            try {
                loginEncrypt.register(userText.getText(), passwordText.getText());
            } catch (RuntimeException e) {
                return;
            }
            successPage.create("注册成功");
            new successPage().launchSuccessPage();
        } else {
            errorPage.create("请输入用户名或密码！");
            new errorPage().launchErrorPage();
        }
    }

    @FXML
    public void backBTNClicked() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("start.fxml"));
        Parent root = loader.load();
        Scene scene = backBTN.getScene();
        scene.setRoot(root);
        primaryStage.setScene(scene);
    }

    @FXML
    public void okBTNClick() {
        if (!loginEncrypt.login(userText.getText(), passwordText.getText())) {
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("control.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            errorPage.create("程序运行出错");
            new errorPage().launchErrorPage();
        }
        Scene scene = okBTN.getScene();
        scene.setRoot(root);
        primaryStage.setScene(scene);
    }

    @FXML
    public void initialize() {
        // 设置回车键事件
        userText.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                passwordText.requestFocus(); // 跳转到下一个文本框
            }
        });
        passwordText.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                okBTNClick();
            }
        });
    }
}
