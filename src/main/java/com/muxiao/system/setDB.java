package com.muxiao.system;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class setDB {
    public static final String PATH = loginEncrypt.basic.desktopPath + "\\宿舍管理系统\\path";
    private final Stage primaryStage = Main.primaryStage;
    @FXML
    private TextField URL;
    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private TextField result;
    @FXML
    private Button okBTN;

    @FXML
    public void okBTNClicked() throws IOException {
        String url = URL.getText();
        String username = this.username.getText();
        String password = this.password.getText();
        try {
            Main.writeBinaryData(PATH, loginEncrypt.ReversibleEncryption.encrypt(url + ";_OwO_;" + username + ";_OwO_;" + password),true);
            String s = loginEncrypt.ReversibleEncryption.decrypt(Main.readBinaryData(PATH));
            String[] strings = s.split(";_OwO_;");
            Main.URL = strings[0];
            Main.USER = strings[1];
            Main.PASSWORD = strings[2];
            Connection connection = Main.getConnection();
            // 在后台线程中执行数据库操作
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(Main.TableCreator::createTables);
            connection.close();
            if(tempUser != null)
                loginEncrypt.deleteUser(tempUser);
            loginEncrypt.register(username, password);
            result.setText("设置成功");
            Main.USER = "MuxiaoWF";
            Main.PASSWORD = "";
        } catch (IOException e) {
            File file = new File(PATH);
            if (file.exists())
                file.delete();
            result.setText("设置失败！(写入文件失败)" + e);
            return;
        } catch (SQLException e) {
            File file = new File(PATH);
            if (file.exists())
                file.delete();
            result.setText("设置失败！(数据库连接失败)" + e);
            return;
        } catch (Exception e) {
            File file = new File(PATH);
            if (file.exists())
                file.delete();
            result.setText("设置失败！(未知错误)" + e);
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("start.fxml"));
        Parent root = loader.load();
        Scene scene = okBTN.getScene();
        scene.setRoot(root);
        primaryStage.setScene(scene);
    }
    String tempUser;
    @FXML
    public void initialize() {
        File file = new File(PATH);
        if (file.exists()) {
            try {
                String s = loginEncrypt.ReversibleEncryption.decrypt(Main.readBinaryData(PATH));
                String[] strings = s.split(";_OwO_;");
                URL.setText(strings[0]);
                username.setText(strings[1]);
                tempUser = username.getText();
            } catch (Exception e) {
                errorPage.create("数据库配置文件读取错误！" + e);
                new errorPage().launchErrorPage();
            }
        } else {
            URL.setText("jdbc:mysql://localhost:3306/");
            username.setText("root");
            tempUser = username.getText();
        }
    }
}
