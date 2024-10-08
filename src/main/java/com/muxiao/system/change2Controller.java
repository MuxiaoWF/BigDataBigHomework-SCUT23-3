package com.muxiao.system;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class change2Controller {
    // 获取哪个表哪个项的数据
    public static String id;
    public static String tableName;
    @FXML
    public TextField text1;
    @FXML
    public TextField text2;
    @FXML
    public TextField text3;
    @FXML
    public TextField text4;
    @FXML
    public TextField text5;
    @FXML
    public TextField text6;
    @FXML
    public TextField text7;
    @FXML
    public TextField column1;
    @FXML
    public TextField column2;
    @FXML
    public TextField column3;
    @FXML
    public TextField column4;
    @FXML
    public TextField column5;
    @FXML
    public TextField column6;
    @FXML
    public TextField column7;
    @FXML
    public TextField result;
    @FXML
    public Button okBTN;
    private int remainingTime = 3; // 倒计时总秒数

    @FXML
    public void okBTNClicked(ActionEvent event) throws InterruptedException {
        String sql;
        switch (tableName) {
            case "students" ->
                sql = "UPDATE students SET " + "student_id = '" + text1.getText() + "'," + "name = '"
                        + text2.getText() + "'," + "gender = '" + text3.getText() + "'," + "major = '"
                        + text4.getText() + "'," + "class = '" + text5.getText() + "'," + "contact = '"
                        + text6.getText() + "'," + "email = '" + text7.getText() + "' WHERE student_id = '" + id + "'";
            case "dormitories" ->
                sql = "UPDATE dormitories SET " + "dormitory_id = " + text1.getText() + "," + "building_name = '"
                        + text2.getText() + "'," + "room_number = " + text3.getText() + "," + "capacity = "
                        + text4.getText() + "," + "status = '" + text5.getText() + "' WHERE dormitory_id = " + id;
            case "repairs" ->
                sql = "UPDATE repairs SET " + "student_id = '" + text1.getText() + "'," + "dormitory_id = "
                        + text2.getText() + "," + "request_date = '" + text3.getText() + "'," + "status = '"
                        + text4.getText() + "' WHERE repair_id = " + id;
            case "violations" ->
                sql = "UPDATE violations SET " + "student_id= '" + text1.getText() + "'," + "dormitory_id = "
                        + text2.getText() + "," + "date = '" + text3.getText() + "'," + "details = '"
                        + text4.getText() + "' WHERE violation_id = " + id;
            case "residences" ->
                sql = "UPDATE residences SET " + "student_id = '" + text1.getText() + "'," + "dormitory_id = "
                        + text2.getText() + "," + "move_in_date = '" + text3.getText() + "'," + "move_out_date = '"
                        + text4.getText() + "' WHERE residence_id = " + id;
            case "fees" ->
                sql = "UPDATE fees SET " + "student_id = '" + text1.getText() + "'," + "fee_id = "
                        + text2.getText() + "," + "payment_date = '" + text3.getText() + "'," + "amount = '"
                        + text4.getText() + "' WHERE fee_id = " + id;
            default -> throw new IllegalArgumentException("Invalid table name: " + tableName);
        }
        try(Connection connection = Main.getConnection();
        Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }catch (Exception e){
            result.setText("执行失败\n" + e);
            return;
        }
        result.setText("执行成功," + remainingTime + "秒后关闭\n");
        change.changed.setValue(true);
        // 创建一个动画，用于倒计时
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), this::updateCountdown));
        timeline.setCycleCount(remainingTime + 1); // 运行次数等于倒计时秒数加一
        timeline.play(); // 开始动画
    }

    public void populate(String tableName, String id) {
        try (Connection connection = Main.getConnection()) {
            ResultSet rs;
            switch (tableName) {
                case "students" -> {
                    try {
                        String query = "SELECT * FROM " + tableName + " WHERE student_id = '" + id + "'";
                        Statement stmt = connection.createStatement();
                        rs = stmt.executeQuery(query);
                    } catch (Exception e) {
                        errorPage.create("查询语句出错\n" + e.getMessage());
                        new errorPage().launchErrorPage();
                        return;
                    }
                    if (rs.next()) {
                        RowData.StudentsRowData rowData = new RowData.StudentsRowData();
                        rowData.setStudent_id(rs.getString("student_id"));
                        rowData.setName(rs.getString("name"));
                        rowData.setGender(rs.getString("gender"));
                        rowData.setMajor(rs.getString("major"));
                        rowData.setClazz(rs.getString("class"));
                        rowData.setContact(rs.getString("contact"));
                        rowData.setEmail(rs.getString("email"));
                        text1.setText(rowData.getStudent_id());
                        column1.setText("学号");
                        column2.setText("姓名");
                        column3.setText("性别");
                        text2.setText(rowData.getName());
                        text3.setText(rowData.getGender());
                        column4.setText("专业");
                        text4.setText(rowData.getMajor());
                        column5.setText("班级");
                        text5.setText(rowData.getClazz());
                        column6.setText("联系方式");
                        text6.setText(rowData.getContact());
                        column7.setText("邮箱");
                        text7.setText(rowData.getEmail());
                    } else {
                        errorPage.create("未找到该记录");
                        new errorPage().launchErrorPage();
                    }
                }
                case "dormitories" -> {
                    try {
                        String query = "SELECT * FROM " + tableName + " WHERE dormitory_id = " + id;
                        Statement stmt = connection.createStatement();
                        rs = stmt.executeQuery(query);
                    } catch (Exception e) {
                        errorPage.create("查询语句出错\n" + e.getMessage());
                        new errorPage().launchErrorPage();
                        return;
                    }
                    RowData.DormitoryRowData rowData = new RowData.DormitoryRowData();
                    rowData.setDormitory_id(rs.getInt("dormitory_id"));
                    rowData.setBuilding_name(rs.getString("building_name"));
                    rowData.setRoom_number(rs.getInt("room_number"));
                    rowData.setCapacity(rs.getInt("capacity"));
                    rowData.setStatus(rs.getString("status"));
                    text1.setText(String.valueOf(rowData.getDormitory_id()));
                    column1.setText("宿舍号");
                    column2.setText("楼号");
                    text2.setText(rowData.getBuilding_name());
                    column3.setText("房间号");
                    text3.setText(String.valueOf(rowData.getRoom_number()));
                    column4.setText("床位数");
                    text4.setText(String.valueOf(rowData.getCapacity()));
                    column5.setText("房间状态");
                    text5.setText(rowData.getStatus());
                    column6.setVisible(false);
                    text6.setVisible(false);
                    column7.setVisible(false);
                    text7.setVisible(false);
                }
                case "residences" -> {
                    try {
                        String query = "SELECT * FROM " + tableName + " WHERE residence_id = " + id;
                        Statement stmt = connection.createStatement();
                        rs = stmt.executeQuery(query);
                    } catch (Exception e) {
                        errorPage.create("查询语句出错\n" + e.getMessage());
                        new errorPage().launchErrorPage();
                        return;
                    }
                    RowData.ResidenceRowData rowData = new RowData.ResidenceRowData();
                    rowData.setResidence_id(rs.getInt("residence_id"));
                    rowData.setStudent_id(rs.getString("student_id"));
                    rowData.setDormitory_id(rs.getInt("dormitory_id"));
                    rowData.setMove_in_date(rs.getString("move_in_date"));
                    rowData.setMove_out_date(rs.getString("move_out_date"));
                    column1.setText("学号");
                    text1.setText(rowData.getStudent_id());
                    column2.setText("宿舍号");
                    text2.setText(String.valueOf(rowData.getDormitory_id()));
                    column3.setText("入住时间");
                    text3.setText(rowData.getMove_in_date());
                    column4.setText("退宿时间");
                    text4.setText(rowData.getMove_out_date());
                    column5.setVisible(false);
                    text5.setVisible(false);
                    column6.setVisible(false);
                    text6.setVisible(false);
                    column7.setVisible(false);
                    text7.setVisible(false);
                }
                case "repairs" -> {
                    try {
                        String query = "SELECT * FROM " + tableName + " WHERE repair_id = " + id;
                        Statement stmt = connection.createStatement();
                        rs = stmt.executeQuery(query);
                    } catch (Exception e) {
                        errorPage.create("查询语句出错\n" + e.getMessage());
                        new errorPage().launchErrorPage();
                        return;
                    }
                    RowData.RepairRowData rowData = new RowData.RepairRowData();
                    rowData.setRepair_id(rs.getInt("repair_id"));
                    rowData.setStudent_id(rs.getString("student_id"));
                    rowData.setDormitory_id(rs.getInt("dormitory_id"));
                    rowData.setRequest_date(rs.getString("request_date"));
                    rowData.setDescription(rs.getString("description"));
                    rowData.setStatus(rs.getString("status"));
                    text1.setText(String.valueOf(rowData.getStudent_id()));
                    column1.setText("学号");
                    column2.setText("宿舍号");
                    text2.setText(String.valueOf(rowData.getDormitory_id()));
                    column3.setText("报修时间");
                    text3.setText(rowData.getRequest_date());
                    column4.setText("问题描述");
                    text4.setText(rowData.getDescription());
                    column5.setText("维修状态");
                    text5.setText(rowData.getStatus());
                    column6.setVisible(false);
                    text6.setVisible(false);
                    column7.setVisible(false);
                    text7.setVisible(false);
                }
                case "violations" -> {
                    try {
                        String query = "SELECT * FROM " + tableName + " WHERE violation_id = " + id;
                        Statement stmt = connection.createStatement();
                        rs = stmt.executeQuery(query);
                    } catch (Exception e) {
                        errorPage.create("查询语句出错\n" + e.getMessage());
                        new errorPage().launchErrorPage();
                        return;
                    }
                    if (rs.next()) {
                        RowData.ViolationRowData rowData = new RowData.ViolationRowData();
                        rowData.setViolation_id(rs.getInt("violation_id"));
                        rowData.setStudent_id(rs.getString("student_id"));
                        rowData.setDormitory_id(rs.getInt("dormitory_id"));
                        rowData.setDate(rs.getString("date"));
                        rowData.setType(rs.getString("type"));
                        rowData.setDetails(rs.getString("details"));
                        text1.setText(String.valueOf(rowData.getStudent_id()));
                        column1.setText("学号");
                        column2.setText("宿舍号");
                        text2.setText(String.valueOf(rowData.getDormitory_id()));
                        column3.setText("违规时间");
                        text3.setText(rowData.getDate());
                        column4.setText("违规类型");
                        text4.setText(rowData.getType());
                        column5.setText("违规内容");
                        text5.setText(rowData.getDetails());
                        column6.setVisible(false);
                        text6.setVisible(false);
                        column7.setVisible(false);
                        text7.setVisible(false);
                    } else {
                        errorPage.create("未找到该记录");
                        new errorPage().launchErrorPage();
                    }
                }
                case "fees" -> {
                    try {
                        String query = "SELECT * FROM " + tableName + " WHERE fee_id = " + id;
                        Statement stmt = connection.createStatement();
                        rs = stmt.executeQuery(query);
                    } catch (Exception e) {
                        errorPage.create("查询语句出错\n" + e.getMessage());
                        new errorPage().launchErrorPage();
                        return;
                    }
                    if (rs.next()) {
                        RowData.FeeRowData rowData = new RowData.FeeRowData();
                        rowData.setFee_id(rs.getString("fee_id"));
                        rowData.setStudent_id(rs.getString("student_id"));
                        rowData.setAmount(rs.getString("amount"));
                        rowData.setPayment_date(rs.getString("payment_date"));
                        rowData.setDescription(rs.getString("description"));
                        text1.setText(rowData.getStudent_id());
                        column1.setText("学号");
                        column2.setText("金额");
                        text2.setText(rowData.getAmount());
                        column3.setText("缴费时间");
                        text3.setText(rowData.getPayment_date());
                        column4.setText("费用描述");
                        text4.setText(rowData.getDescription());
                        column5.setVisible(false);
                        text5.setVisible(false);
                        column6.setVisible(false);
                        text6.setVisible(false);
                        column7.setVisible(false);
                        text7.setVisible(false);
                    } else {
                        errorPage.create("未找到该记录");
                        new errorPage().launchErrorPage();
                    }
                }
            }
        } catch (Exception e) {
            errorPage.create("查询时出错\n" + e.getMessage());
            new errorPage().launchErrorPage();
        }
    }

    private void updateCountdown(ActionEvent event) {
        remainingTime--;
        if (remainingTime > 0) {
            result.setText("执行成功," + remainingTime + "秒后关闭\n");
        } else {
            Stage stage = (Stage) okBTN.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    public void initialize() {
        populate(tableName, id);
    }
}
