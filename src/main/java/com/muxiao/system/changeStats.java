package com.muxiao.system;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class changeStats {
    private final Stage primaryStage = Main.primaryStage;
    @FXML
    private TextArea result;
    @FXML
    private Button okBTN;
    @FXML
    private ListView<String> list;
    @FXML
    private TextField text;
    @FXML
    private TextField stats;
    @FXML
    private Button backBTN;
    @FXML
    private ComboBox<String> tableNameComboBox;
    private String tableName;
    private Map<String, String> columnTypeMap;

    @FXML
    public void backBTNClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("control.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
    }

    @FXML
    public void okBTNClicked(ActionEvent event) {
        String sql = "alter table " + tableName + " modify column " + list.getSelectionModel().getSelectedItem() + " " + text.getText() + " ;";
        try (Connection connection = Main.getConnection();
             Statement statement =connection.createStatement()){
            statement.executeUpdate(sql);
        }catch (Exception e){
            result.setText("执行失败\n" + e);
            return;
        }
        result.setText("执行成功\n"+sql);
        //更新stats状态
        getList();
        stats.setText(columnTypeMap.get(list.getSelectionModel().getSelectedItem()));
    }

    @FXML
    public void initialize() {
        tableNameComboBox.setItems(FXCollections.observableArrayList("学生信息", "宿舍信息", "入住信息", "维修信息", "费用信息", "违规信息"));
        tableNameComboBoxChanged();
        tableNameComboBox.setValue("学生信息");
    }

    private void tableNameComboBoxChanged() {
        tableNameComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case "学生信息":
                    tableName = "students";
                    list.setItems(FXCollections.observableArrayList(Objects.requireNonNull(getList())));
                    listNameChanged();
                    list.getSelectionModel().select(0);
                    break;
                case "宿舍信息":
                    tableName = "dormitories";
                    list.setItems(FXCollections.observableArrayList(Objects.requireNonNull(getList())));
                    list.getSelectionModel().select(0);
                    break;
                case "入住信息":
                    tableName = "residences";
                    list.setItems(FXCollections.observableArrayList(Objects.requireNonNull(getList())));
                    list.getSelectionModel().select(0);
                    break;
                case "维修信息":
                    tableName = "repairs";
                    list.setItems(FXCollections.observableArrayList(Objects.requireNonNull(getList())));
                    list.getSelectionModel().select(0);
                    break;
                case "费用信息":
                    tableName = "fees";
                    list.setItems(FXCollections.observableArrayList(Objects.requireNonNull(getList())));
                    list.getSelectionModel().select(0);
                    break;
                case "违规信息":
                    tableName = "violations";
                    list.setItems(FXCollections.observableArrayList(Objects.requireNonNull(getList())));
                    list.getSelectionModel().select(0);
                    break;
            }
        });
    }

    private ObservableList<String> getList() {
        ObservableList<String> observableColumns = FXCollections.observableArrayList();
        columnTypeMap = new HashMap<>();
        try (Connection connection = Main.getConnection();
             Statement statement = connection.createStatement()) {
            String query = "SELECT COLUMN_NAME, COLUMN_TYPE " +
                    "FROM information_schema.columns " +
                    "WHERE TABLE_NAME = '" + tableName + "'";
            ResultSet listSet = statement.executeQuery(query);
            while (listSet.next()) {
                String columnName = listSet.getString("COLUMN_NAME");
                observableColumns.add(columnName);
                String dataType = listSet.getString("COLUMN_TYPE");
                columnTypeMap.put(columnName, dataType);
            }
        } catch (SQLException e) {
            result.setText("查询失败\n" + e);
            return null;
        }
        return observableColumns;
    }

    private void listNameChanged() {
        list.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            stats.setText(columnTypeMap.get(newValue));
        });
    }
}
