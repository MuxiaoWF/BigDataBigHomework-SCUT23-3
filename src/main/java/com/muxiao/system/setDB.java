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
    public static final String PATH = loginEncrypt.basic.desktopPath + "\\宿舍管理系统\\path";
    @FXML
    public void okBTNClicked() throws IOException {
        String url = URL.getText();
        String username = this.username.getText();
        String password = this.password.getText();
        try {
            Main.writeBinaryData(PATH,loginEncrypt.ReversibleEncryption.encrypt(url + ";_OwO_;" + username + ";_OwO_;" + password));
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
            loginEncrypt.register(username, password);
            result.setText("设置成功");
            Main.USER = "MuxiaoWF";
            Main.PASSWORD = "";
        } catch (IOException e) {
            File file = new File(PATH);
            if (file.exists())
                file.delete();
            result.setText("设置失败！(写入文件失败)");
            return;
        } catch (SQLException e) {
            File file = new File(PATH);
            if (file.exists())
                file.delete();
            result.setText("设置失败！(数据库连接失败)");
            return;
        } catch (Exception e) {
            File file = new File(PATH);
            if (file.exists())
                file.delete();
            result.setText("设置失败！(未知错误)");
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("start.fxml"));
        Parent root = loader.load();
        Scene scene = okBTN.getScene();
        scene.setRoot(root);
        primaryStage.setScene(scene);
    }

    @FXML
    public void initialize() {
        URL.setText("jdbc:mysql://localhost:3306/");
        username.setText("root");
    }
}
