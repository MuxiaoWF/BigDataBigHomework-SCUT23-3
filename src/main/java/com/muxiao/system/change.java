package com.muxiao.system;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;

//调用check
public class change {
    public static BooleanProperty changed = new SimpleBooleanProperty(false);
    private final Stage primaryStage = Main.primaryStage;
    @FXML
    private TableView<RowData> tableView;
    @FXML
    private Button okBTN;
    @FXML
    private ComboBox<String> tableNameComboBox;
    @FXML
    private Button backBTN;
    @FXML
    private Button changeBTN;
    @FXML
    private TextField result;
    @FXML
    private TextField require;
    @FXML
    public void okBTNClicked() {
        String tableName = check.switchTableName(tableNameComboBox.getValue());
        ObservableList<RowData> data = FXCollections.observableArrayList();
        // 设置表格列名
        check.setColumnNames(tableView, tableName);
        // 查询表数据并填充表格
        check.populateTable(tableName, data, tableView, require.getText());
    }

    @FXML
    public void initialize() {
        tableNameComboBox.setItems(FXCollections.observableArrayList("学生信息", "宿舍信息", "入住信息", "维修信息", "费用信息", "违规信息"));
        tableNameComboBox.setValue("学生信息");
        changed.addListener((observable, oldValue, newValue) -> {
            if(newValue){
                okBTNClicked();//相当于刷新
                changed.set(false);
            }
        });
        require.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                okBTNClicked();
            }
        });
    }

    @FXML
    public void backBTNClicked() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("control.fxml"));
        Parent root = loader.load();
        Scene scene = backBTN.getScene();
        scene.setRoot(root);
        primaryStage.setScene(scene);
    }

    @FXML
    private void changeBTNClicked() {
        switch (tableNameComboBox.getValue()) {
            case "学生信息":
                RowData.StudentsRowData id = (RowData.StudentsRowData) tableView.getSelectionModel().getSelectedItem();
                change2Controller.id = id.getStudent_id();
                change2Controller.tableName = "students";
                break;
            case "宿舍信息":
                RowData.DormitoryRowData id2 = (RowData.DormitoryRowData) tableView.getSelectionModel().getSelectedItem();
                change2Controller.id = String.valueOf(id2.getDormitory_id());
                change2Controller.tableName = "dormitories";
                break;
            case "入住信息":
                RowData.ResidenceRowData id3 = (RowData.ResidenceRowData) tableView.getSelectionModel().getSelectedItem();
                change2Controller.id = String.valueOf(id3.getResidence_id());
                change2Controller.tableName = "residents";
                break;
            case "维修信息":
                RowData.RepairRowData id4 = (RowData.RepairRowData) tableView.getSelectionModel().getSelectedItem();
                change2Controller.id = String.valueOf(id4.getRepair_id());
                change2Controller.tableName = "repairs";
                break;
            case "费用信息":
                RowData.FeeRowData id5 = (RowData.FeeRowData) tableView.getSelectionModel().getSelectedItem();
                change2Controller.id = id5.getFee_id();
                change2Controller.tableName = "fees";
                break;
            case "违规信息":
                RowData.ViolationRowData id6 = (RowData.ViolationRowData) tableView.getSelectionModel().getSelectedItem();
                change2Controller.id = String.valueOf(id6.getViolation_id());
                change2Controller.tableName = "violations";
                break;
            default:
                result.setText("请选择要修改的");
                return;
        }
        new change2().launchPage();
    }
}
