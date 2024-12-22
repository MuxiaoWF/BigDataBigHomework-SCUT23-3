package com.muxiao.system;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.muxiao.system.Main.primaryStage;

public class admin {
    @FXML
    private CheckBox select;
    @FXML
    private CheckBox update;
    @FXML
    private CheckBox delete;
    @FXML
    private CheckBox insert;
    @FXML
    private Button backBTN;
    @FXML
    private Button registerBTN;
    @FXML
    private TextField user;
    @FXML
    private TextField password;
    @FXML
    private Button deleteBTN;
    @FXML
    private ComboBox<String> userList;

    @FXML
    private void deleteBTNClicked() {
        if (userList.getValue() == null) {
            errorPage.create("请选择用户名！");
            new errorPage().launchErrorPage();
            return;
        }
        try (Statement statement = Main.getConnection().createStatement()) {
            String sql = "drop user '" + userList.getValue() + "_muxiao'@'%';";
            statement.execute(sql);
            loginEncrypt.deleteUser(userList.getValue());
        } catch (SQLException e) {
            errorPage.create("删除失败\n" + e);
            new errorPage().launchErrorPage();
            return;
        }
        userList.getItems().remove(userList.getValue());
        successPage.create("删除成功");
        new successPage().launchSuccessPage();
    }

    @FXML
    private void backBTNClicked() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("control.fxml"));
        Parent root = loader.load();
        Scene scene = backBTN.getScene();
        scene.setRoot(root);
        primaryStage.setScene(scene);
    }

    @FXML
    public void registerBTNClicked() {
        String temp = "";
        if (select.isSelected())
            temp += "select,";
        if (update.isSelected())
            temp += "update,";
        if (delete.isSelected())
            temp += "delete,";
        if (insert.isSelected())
            temp += "insert,";
        if (temp.isEmpty()) {
            errorPage.create("权限不能为空！");
            new errorPage().launchErrorPage();
            return;
        }
        if (!user.getText().isEmpty() && !password.getText().isEmpty()) {
            try (Statement statement = Main.getConnection().createStatement()) {
                String sql = "CREATE USER IF NOT EXISTS '" + user.getText() + "_muxiao'@'%' IDENTIFIED BY '" + password.getText() + "';";
                String grant = "grant " + temp.substring(0, temp.length() - 1) + " on dormitory.* to '" + user.getText() + "_muxiao'@'%';";
                statement.execute(sql);
                statement.execute(grant);
                loginEncrypt.register(user.getText(), password.getText());
            } catch (RuntimeException | SQLException e) {
                errorPage.create("注册失败\n" + e);
                new errorPage().launchErrorPage();
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
    public void initialize() {
        try (Statement stmt = Main.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery("select user from mysql.user");
            while (rs.next()) {
                if (rs.getString("user").contains("_muxiao"))
                    userList.getItems().add(rs.getString("user").replace("_muxiao", ""));
            }
        } catch (SQLException e) {
            errorPage.create("获取用户列表失败\n" + e);
            new errorPage().launchErrorPage();
        }
    }
}
