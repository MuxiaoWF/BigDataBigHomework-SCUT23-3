package com.muxiao.system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class login {
    private final Stage primaryStage=Main.primaryStage;
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
    public void registerBTNClick(ActionEvent event) throws IOException {
        if(!userText.getText().isEmpty()&&!passwordText.getText().isEmpty()) {
            try {
                loginEncrypt.register(userText.getText(), passwordText.getText());
            } catch (RuntimeException e) {
                return;
            }
            errorPage.create("注册成功");
            new errorPage().launchErrorPage();
        }else {
            errorPage.create("请输入用户名或密码！");
            new errorPage().launchErrorPage();
        }
    }
    @FXML
    public void backBTNClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("start.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
    }
    @FXML
    public void okBTNClick(ActionEvent event) throws IOException {
        if (!loginEncrypt.login(userText.getText(), passwordText.getText())) {
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("control.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
    }
}
