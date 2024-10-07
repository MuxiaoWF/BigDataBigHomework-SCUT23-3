package com.muxiao.system;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Objects;

public class add {
    private final Stage primaryStage = Main.primaryStage;
    @FXML
    private ComboBox<String> tableNameComboBox;
    @FXML
    private Button backBTN;
    @FXML
    private Button okBTN;
    @FXML
    private TextField text1;
    @FXML
    private TextField text2;
    @FXML
    private TextField text3;
    @FXML
    private TextField text4;
    @FXML
    private TextField text5;
    @FXML
    private TextField text6;
    @FXML
    private TextField text7;
    @FXML
    private TextField column1;
    @FXML
    private TextField column2;
    @FXML
    private TextField column3;
    @FXML
    private TextField column4;
    @FXML
    private TextField column5;
    @FXML
    private TextField column6;
    @FXML
    private TextField column7;
    @FXML
    private TextField result;
    private String tableName;

    @FXML
    private void backBTNClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("control.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
    }

    @FXML
    private void okBTNClicked(ActionEvent event) {
        try (Connection connection = Main.getConnection();
             Statement stmt = connection.createStatement()) {
            String sql = switch (tableName) {
                case "students" -> {
                    String student_id = text1.getText().isEmpty() ? "NULL" : "'" + text1.getText() + "'";
                    String name = text2.getText().isEmpty() ? "NULL" : "'" + text2.getText() + "'";
                    String gender = text3.getText().isEmpty() ? "NULL" : "'" + text3.getText() + "'";
                    String major = text4.getText().isEmpty() ? "NULL" : "'" + text4.getText() + "'";
                    String _class = text5.getText().isEmpty() ? "NULL" : "'" + text5.getText() + "'";
                    String contact = text6.getText().isEmpty() ? "NULL" : "'" + text6.getText() + "'";
                    String email = text7.getText().isEmpty() ? "NULL" : "'" + text7.getText() + "'";

                    yield "INSERT INTO students (student_id, name, gender, major, class, contact, email) VALUES (" +
                            student_id + ", " + name + ", " + gender + ", " + major + ", " + _class + ", " + contact + ", " + email + ")";
                }
                case "dormitories" -> {
                    int dormitory_id = text1.getText().isEmpty() ? 0 : Integer.parseInt(text1.getText());
                    String building_name = text2.getText().isEmpty() ? "NULL" : "'" + text2.getText() + "'";
                    int room_number = text3.getText().isEmpty() ? 0 : Integer.parseInt(text3.getText());
                    int capacity = text4.getText().isEmpty() ? 0 : Integer.parseInt(text4.getText());
                    String status = text5.getText().isEmpty() ? "NULL" : "'" + text5.getText() + "'";

                    yield "INSERT INTO dormitories (dormitory_id, building_name, room_number, capacity, status) VALUES (" +
                            dormitory_id + ", " + building_name + ", " + room_number + ", " + capacity + ", " + status + ")";
                }
                case "repairs" -> {
                    String student_id = text1.getText().isEmpty() ? "NULL" : "'" + text1.getText() + "'";
                    int dormitory_id = text2.getText().isEmpty() ? 0 : Integer.parseInt(text2.getText());
                    String request_date = text3.getText().isEmpty() ? "NULL" : "'" + text3.getText() + "'";
                    String description = text4.getText().isEmpty() ? "NULL" : "'" + text4.getText() + "'";
                    String status = text5.getText().isEmpty() ? "NULL" : "'" + text5.getText() + "'";

                    yield "INSERT INTO repairs (student_id, dormitory_id, request_date, description, status) VALUES (" +
                            student_id + ", " + dormitory_id + ", " + request_date + ", " + description + ", " + status + ")";
                }
                case "fees" -> {
                    String student_id = text1.getText().isEmpty() ? "NULL" : "'" + text1.getText() + "'";
                    double amount = text2.getText().isEmpty() ? 0.0 : Double.parseDouble(text2.getText());
                    String payment_date = text3.getText().isEmpty() ? "NULL" : "'" + text3.getText() + "'";
                    String description = text4.getText().isEmpty() ? "NULL" : "'" + text4.getText() + "'";

                    yield "INSERT INTO fees (student_id, amount, payment_date, description) VALUES (" +
                            student_id + ", " + amount + ", " + payment_date + ", " + description + ")";
                }
                case "violations" -> {
                    String student_id = text1.getText().isEmpty() ? "NULL" : "'" + text1.getText() + "'";
                    int dormitory_id = text2.getText().isEmpty() ? 0 : Integer.parseInt(text2.getText());
                    String date = text3.getText().isEmpty() ? "NULL" : "'" + text3.getText() + "'";
                    String type = text4.getText().isEmpty() ? "NULL" : "'" + text4.getText() + "'";
                    String details = text5.getText().isEmpty() ? "NULL" : "'" + text5.getText() + "'";

                    yield "INSERT INTO violations (student_id, dormitory_id, date, type, details) VALUES (" +
                            student_id + ", " + dormitory_id + ", " + date + ", " + type + ", " + details + ")";
                }
                case "residences" -> {
                    String student_id = text1.getText().isEmpty() ? "NULL" : "'" + text1.getText() + "'";
                    int dormitory_id = text2.getText().isEmpty() ? 0 : Integer.parseInt(text2.getText());
                    String move_in_date = text3.getText().isEmpty() ? "NULL" : "'" + text3.getText() + "'";
                    String move_out_date = text4.getText().isEmpty() ? "NULL" : "'" + text4.getText() + "'";

                    yield "INSERT INTO residences (student_id, dormitory_id, move_in_date, move_out_date) VALUES (" +
                            student_id + ", " + dormitory_id + ", " + move_in_date + ", " + move_out_date + ")";
                }
                default -> "";
            };
            // 执行插入操作
            int rowsAffected = stmt.executeUpdate(sql);
            if (rowsAffected > 0) {
                result.setText("添加成功");
                cleanText();
            } else {
                result.setText("添加失败，可能是因为主键冲突或其他约束条件");
            }
        } catch (Exception e) {
            result.setText("添加失败\n" + e.getMessage());
        }
    }
    private void cleanText(){
        text1.clear();
        text2.clear();
        text3.clear();
        text4.clear();
        text5.clear();
        text6.clear();
        text7.clear();
    }
    private void tableNameComboBoxChanged() {
        // 设置监听器以响应选项变化
        tableNameComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            cleanText();
            switch (newValue) {
                case "学生信息":
                    column1.setText("学号");
                    text1.setPromptText("12位学号");
                    column2.setText("姓名");
                    text2.setPromptText("学生姓名");
                    column3.setText("性别");
                    text3.setPromptText("男或女");
                    column4.setText("专业");
                    text4.setPromptText("专业名称");
                    column5.setVisible(true);
                    column5.setText("班级");
                    text5.setVisible(true);
                    text5.setPromptText("班级名称");
                    column6.setVisible(true);
                    column6.setText("联系方式");
                    text6.setVisible(true);
                    text6.setPromptText("联系方式");
                    column7.setVisible(true);
                    column7.setText("邮箱");
                    text7.setVisible(true);
                    text7.setPromptText("邮箱地址");
                    tableName = "students";
                    break;
                case "宿舍信息":
                    column1.setText("宿舍id");
                    text1.setPromptText("建议楼号+宿舍（07215）");
                    column2.setText("楼号");
                    text2.setPromptText("c7");
                    column3.setText("房间号");
                    text3.setPromptText("房间号");
                    column4.setText("床位数");
                    text4.setPromptText("床位数");
                    column5.setVisible(true);
                    column5.setText("房间状态");
                    text5.setVisible(true);
                    text5.setPromptText("占用或空");
                    column6.setVisible(false);
                    text6.setVisible(false);
                    column7.setVisible(false);
                    text7.setVisible(false);
                    tableName = "dormitories";
                    break;
                case "维修信息":
                    column1.setText("学号");
                    text1.setPromptText("12位学号");
                    column2.setText("宿舍id");
                    text2.setPromptText("【自设】楼号+宿舍（07215）");
                    column3.setText("维修时间");
                    text3.setPromptText("维修时间");
                    column4.setText("维修内容");
                    text4.setPromptText("维修内容");
                    column5.setVisible(true);
                    column5.setText("维修状态");
                    text5.setVisible(true);
                    text5.setPromptText("维修状态");
                    column6.setVisible(false);
                    text6.setVisible(false);
                    column7.setVisible(false);
                    text7.setVisible(false);
                    tableName = "repairs";
                    break;
                case "费用信息":
                    column1.setText("学号");
                    text1.setPromptText("12位学号");
                    column2.setText("费用金额");
                    text2.setPromptText("费用金额");
                    column3.setText("费用时间");
                    text3.setPromptText("费用时间");
                    column4.setText("费用描述");
                    text4.setPromptText("【可选】费用描述");
                    column5.setVisible(false);
                    text5.setVisible(false);
                    column6.setVisible(false);
                    text6.setVisible(false);
                    column7.setVisible(false);
                    text7.setVisible(false);
                    tableName = "fees";
                    break;
                case "违规信息":
                    column1.setText("学号");
                    text1.setPromptText("12位学号");
                    column2.setText("宿舍id");
                    text2.setPromptText("【自设】楼号+宿舍（07215）");
                    column3.setText("违规时间");
                    text3.setPromptText("违规时间");
                    column4.setText("违规类型");
                    text4.setPromptText("违规类型");
                    column5.setVisible(true);
                    column5.setText("违规内容");
                    text5.setVisible(true);
                    text5.setPromptText("违规内容");
                    column6.setVisible(false);
                    text6.setVisible(false);
                    column7.setVisible(false);
                    text7.setVisible(false);
                    tableName = "violations";
                    break;
                case "入住信息":
                    column1.setText("学号");
                    text1.setPromptText("12位学号");
                    column2.setText("宿舍id");
                    text2.setPromptText("【自设】楼号+宿舍（07215）");
                    column3.setText("入住时间");
                    text3.setPromptText("入住时间");
                    column4.setText("退宿时间");
                    text4.setPromptText("退宿时间");
                    column5.setVisible(false);
                    text5.setVisible(false);
                    column6.setVisible(false);
                    text6.setVisible(false);
                    column7.setVisible(false);
                    text7.setVisible(false);
                    tableName = "residences";
                    break;
            }
        });
    }

    @FXML
    public void initialize() {
        tableNameComboBox.setItems(FXCollections.observableArrayList("学生信息", "宿舍信息", "入住信息", "维修信息", "费用信息", "违规信息"));
        tableNameComboBoxChanged();
        tableNameComboBox.setValue("学生信息");
    }
}
