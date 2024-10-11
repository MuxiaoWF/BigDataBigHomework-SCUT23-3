package com.muxiao.system;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;

//调用check
public class delete {
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
    private Button deleteBTN;
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
    private void deleteBTNClicked() {
        String sql;
        switch (tableNameComboBox.getValue()) {
            case "学生信息":
                RowData.StudentsRowData id = (RowData.StudentsRowData) tableView.getSelectionModel().getSelectedItem();
                sql = "DELETE FROM students WHERE student_id = " + id.getStudent_id() + " ;";
                break;
            case "宿舍信息":
                RowData.DormitoryRowData id2 = (RowData.DormitoryRowData) tableView.getSelectionModel().getSelectedItem();
                sql = "DELETE FROM dormitories WHERE dormitory_id = " + id2.getDormitory_id() + " ;";
                break;
            case "入住信息":
                RowData.ResidenceRowData id3 = (RowData.ResidenceRowData) tableView.getSelectionModel().getSelectedItem();
                sql = "DELETE FROM residents WHERE residence_id = " + id3.getResidence_id() + " ;";
                break;
            case "维修信息":
                RowData.RepairRowData id4 = (RowData.RepairRowData) tableView.getSelectionModel().getSelectedItem();
                sql = "DELETEFROM repairs WHERE repair_id = " + id4.getRepair_id() + " ;";
                break;
            case "费用信息":
                RowData.FeeRowData id5 = (RowData.FeeRowData) tableView.getSelectionModel().getSelectedItem();
                sql = "DELETE FROM fees WHERE fee_id = " + id5.getFee_id() + " ;";
                break;
            case "违规信息":
                RowData.ViolationRowData id6 = (RowData.ViolationRowData) tableView.getSelectionModel().getSelectedItem();
                sql = "DELETE FROM violations WHERE violation_id = " + id6.getViolation_id() + " ;";
                break;
            default:
                result.setText("请选择要删除的");
                return;
        }
        try (Connection conn = Main.getConnection();
             Statement statement = conn.createStatement()) {
            statement.executeUpdate(sql);
        } catch (Exception e) {
            result.setText("删除失败\n" + e);
            return;
        }
        result.setText("删除成功");
        okBTNClicked();//刷新表
    }
}
