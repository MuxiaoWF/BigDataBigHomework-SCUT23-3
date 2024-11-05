package com.muxiao.system;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;

public class login {
    public static boolean isAdmin = false;
    private final Stage primaryStage = Main.primaryStage;
    private final Stage stage = loginAdmin.stage;
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
    private Label title;

    @FXML
    public void registerBTNClick() {
        try {
            new loginAdmin().start(new Stage());
        } catch (Exception e) {
            errorPage.create("注册页面出错\n" + e);
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
        if (!isAdmin) {
            if (!loginEncrypt.login(userText.getText(), passwordText.getText())) {
                return;
            }
        } else {
            if (!loginEncrypt.loginAdmin(userText.getText(), passwordText.getText())) {
                return;
            }
            stage.close();
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
        if (isAdmin) {
            registerBTN.setVisible(false);
            title.setText("管理员登录");
            userText.setPromptText("管理员用户名");
            passwordText.setPromptText("管理员密码");
        }
    }
}
