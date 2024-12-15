package com.muxiao.system;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

import static com.muxiao.system.importDB.*;

public class control {
    private static final Stage primaryStage = Main.primaryStage;
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
    private Button adminBTN;
    @FXML
    private Button importFileBTN;

    @FXML
    private void importFileBTNClicked() {
        // 获取当前舞台（Stage）
        Stage stage = (Stage) importFileBTN.getScene().getWindow();

        // 创建文件选择器
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择要导入的文件");

        // 设置文件过滤器，只显示特定类型的文件
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("HTML Files (*.html)", "*.html"));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("SQL Files (*.sql)", "*.sql"));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files (*.xlsx)", "*.xlsx"));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("XML Files (*.xml)", "*.xml"));

        // 显示文件选择对话框并获取所选文件
        File selectedFile = fileChooser.showOpenDialog(stage);

        // 检查用户是否选择了文件
        String filePath = selectedFile.getAbsolutePath();
        // 根据文件类型调用相应的导入方法
        if (filePath.endsWith(".html")) {
            successPage.create(importHTML(filePath));
            new successPage().launchSuccessPage();
        } else if (filePath.endsWith(".sql")) {
            successPage.create(importSQL(filePath));
            new successPage().launchSuccessPage();
        } else if (filePath.endsWith(".xlsx")) {
            successPage.create(importExcel(filePath));
            new successPage().launchSuccessPage();
        } else if (filePath.endsWith(".xml")) {
            successPage.create(importXML(filePath));
            new successPage().launchSuccessPage();
        }
    }

    @FXML
    private void adminBTNClicked() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("admin.fxml"));
        Parent root = loader.load();
        Scene scene = adminBTN.getScene();
        scene.setRoot(root);
        primaryStage.setScene(scene);
    }

    @FXML
    public void backBTNClicked() throws IOException {
        login.isAdmin = false;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = loader.load();
        Scene scene = backBTN.getScene();
        scene.setRoot(root);
        primaryStage.setScene(scene);
        Main.USER = "MuxiaoWF";
        Main.PASSWORD = "";
    }

    @FXML
    private void addBTNClicked() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("add.fxml"));
        Parent root = loader.load();
        Scene scene = addBTN.getScene();
        scene.setRoot(root);
        primaryStage.setScene(scene);
    }

    @FXML
    private void changeBTNClicked() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("change.fxml"));
        Parent root = loader.load();
        Scene scene = changeBTN.getScene();
        scene.setRoot(root);
        primaryStage.setScene(scene);
    }

    @FXML
    private void deleteBTNClicked() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("delete.fxml"));
        Parent root = loader.load();
        Scene scene = deleteBTN.getScene();
        scene.setRoot(root);
        primaryStage.setScene(scene);
    }

    @FXML
    private void changeStatsBTNClicked() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("changeStats.fxml"));
        Parent root = loader.load();
        Scene scene = changeBTN.getScene();
        scene.setRoot(root);
        primaryStage.setScene(scene);
    }

    @FXML
    private void checkBTNClicked() throws IOException {
        check.FromPage = "control.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource("check.fxml"));
        Parent root = loader.load();
        Scene scene = checkBTN.getScene();
        scene.setRoot(root);
        primaryStage.setScene(scene);
    }

    @FXML
    private void initialize() {
        adminBTN.setVisible(login.isAdmin);
    }
}
