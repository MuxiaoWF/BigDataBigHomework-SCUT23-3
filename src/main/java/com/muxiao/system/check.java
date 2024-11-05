package com.muxiao.system;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Objects;

public class check {
    public static String FromPage;
    private static boolean temp = true;//检查条件语句是否合格
    private final Stage primaryStage = Main.primaryStage;
    @FXML
    private TableView<RowData> tableView;
    @FXML
    private Button okBTN;
    @FXML
    private Button exportBTN;
    @FXML
    private ComboBox<String> tableNameComboBox;
    @FXML
    private Button backBTN;
    @FXML
    private TextField require;
    @FXML
    private TextField result;

    public static String switchTableName(String tableName) {
        switch (tableName) {
            case "学生信息" -> tableName = "students";
            case "宿舍信息" -> tableName = "dormitories";
            case "入住信息" -> tableName = "residences_v";
            case "维修信息" -> tableName = "repairs_v";
            case "费用信息" -> tableName = "fees_v";
            case "违规信息" -> tableName = "violations_v";
            default -> throw new IllegalArgumentException("Invalid table name: " + tableName);
        }
        return tableName;
    }

    public static void setColumnNames(TableView<RowData> tableView, String tableName) {
        // 清空现有的列
        tableView.getColumns().clear();
        // 根据表名设置列名
        switch (tableName) {
            case "students" -> {
                TableColumn<RowData, String> studentIdCol = new TableColumn<>("学号(student_id)");
                studentIdCol.setCellValueFactory(new PropertyValueFactory<>("student_id"));

                TableColumn<RowData, String> nameCol = new TableColumn<>("姓名(name)");
                nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

                TableColumn<RowData, String> genderCol = new TableColumn<>("性别(gender)");
                genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));

                TableColumn<RowData, String> majorCol = new TableColumn<>("专业(major)");
                majorCol.setCellValueFactory(new PropertyValueFactory<>("major"));

                TableColumn<RowData, String> classCol = new TableColumn<>("班级(class)");
                classCol.setCellValueFactory(new PropertyValueFactory<>("clazz"));

                TableColumn<RowData, String> contactCol = new TableColumn<>("联系方式(contact)");
                contactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));

                TableColumn<RowData, String> emailCol = new TableColumn<>("邮箱(email)");
                emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

                tableView.getColumns().addAll(studentIdCol, nameCol, genderCol, majorCol, classCol, contactCol, emailCol);
            }
            case "dormitories" -> {
                TableColumn<RowData, Integer> dormitoryIdCol = new TableColumn<>("宿舍id(dormitory_id)");
                dormitoryIdCol.setCellValueFactory(new PropertyValueFactory<>("dormitory_id"));

                TableColumn<RowData, String> buildingNameCol = new TableColumn<>("楼栋号(building_name)");
                buildingNameCol.setCellValueFactory(new PropertyValueFactory<>("building_name"));

                TableColumn<RowData, Integer> roomNumberCol = new TableColumn<>("宿舍号(room_number)");
                roomNumberCol.setCellValueFactory(new PropertyValueFactory<>("room_number"));

                TableColumn<RowData, Integer> capacityCol = new TableColumn<>("床位数(capacity)");
                capacityCol.setCellValueFactory(new PropertyValueFactory<>("capacity"));

                TableColumn<RowData, String> statusCol = new TableColumn<>("入住状态(status)");
                statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

                tableView.getColumns().addAll(dormitoryIdCol, buildingNameCol, roomNumberCol, capacityCol, statusCol);
            }
            case "residences_v" -> {
                TableColumn<RowData, Integer> residenceIdCol = new TableColumn<>("序号(residence_id)");
                residenceIdCol.setCellValueFactory(new PropertyValueFactory<>("residence_id"));

                TableColumn<RowData, String> studentIdCol = new TableColumn<>("学号(student_id)");
                studentIdCol.setCellValueFactory(new PropertyValueFactory<>("student_id"));

                TableColumn<RowData, String> nameCol = new TableColumn<>("姓名(name)");
                nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

                TableColumn<RowData, Integer> dormitoryIdCol = new TableColumn<>("宿舍id(dormitory_id)");
                dormitoryIdCol.setCellValueFactory(new PropertyValueFactory<>("dormitory_id"));


                TableColumn<RowData, String> moveInDateCol = new TableColumn<>("入住日期(move_in_date)");
                moveInDateCol.setCellValueFactory(new PropertyValueFactory<>("move_in_date"));

                TableColumn<RowData, String> moveOutDateCol = new TableColumn<>("退住日期(move_out_date)");
                moveOutDateCol.setCellValueFactory(new PropertyValueFactory<>("move_out_date"));

                tableView.getColumns().addAll(residenceIdCol, studentIdCol, nameCol,dormitoryIdCol, moveInDateCol, moveOutDateCol);
            }
            case "repairs_v" -> {
                TableColumn<RowData, Integer> repairIdCol = new TableColumn<>("序号(repair_id)");
                repairIdCol.setCellValueFactory(new PropertyValueFactory<>("repair_id"));

                TableColumn<RowData, String> studentIdCol = new TableColumn<>("学号(student_id)");
                studentIdCol.setCellValueFactory(new PropertyValueFactory<>("student_id"));

                TableColumn<RowData, String> nameCol = new TableColumn<>("姓名(name)");
                nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

                TableColumn<RowData, Integer> dormitoryIdCol = new TableColumn<>("宿舍id(dormitory_id)");
                dormitoryIdCol.setCellValueFactory(new PropertyValueFactory<>("dormitory_id"));

                TableColumn<RowData, String> requestDateCol = new TableColumn<>("请求日期(request_date)");
                requestDateCol.setCellValueFactory(new PropertyValueFactory<>("request_date"));

                TableColumn<RowData, String> descriptionCol = new TableColumn<>("附加描述(description)");
                descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));

                TableColumn<RowData, String> statusCol = new TableColumn<>("状态(status)");
                statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
                tableView.getColumns().addAll(repairIdCol, studentIdCol, nameCol,dormitoryIdCol, requestDateCol, descriptionCol, statusCol);
            }
            case "violations_v" -> {
                TableColumn<RowData, Integer> violationIdCol = new TableColumn<>("序号(violation_id)");
                violationIdCol.setCellValueFactory(new PropertyValueFactory<>("violation_id"));

                TableColumn<RowData, String> studentIdCol = new TableColumn<>("学号(student_id)");
                studentIdCol.setCellValueFactory(new PropertyValueFactory<>("student_id"));

                TableColumn<RowData, String> nameCol = new TableColumn<>("姓名(name)");
                nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

                TableColumn<RowData, Integer> dormitoryIdCol = new TableColumn<>("宿舍id(dormitory_id)");
                dormitoryIdCol.setCellValueFactory(new PropertyValueFactory<>("dormitory_id"));

                TableColumn<RowData, String> dateCol = new TableColumn<>("日期(date)");
                dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

                TableColumn<RowData, String> typeCol = new TableColumn<>("类型(type)");
                typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

                TableColumn<RowData, String> detailsCol = new TableColumn<>("详细描述(details)");
                detailsCol.setCellValueFactory(new PropertyValueFactory<>("details"));

                tableView.getColumns().addAll(violationIdCol, studentIdCol, nameCol,dormitoryIdCol, dateCol, typeCol, detailsCol);
            }
            case "fees_v" -> {
                TableColumn<RowData, Integer> feeIdCol = new TableColumn<>("序号(fee_id)");
                feeIdCol.setCellValueFactory(new PropertyValueFactory<>("fee_id"));

                TableColumn<RowData, String> studentIdCol = new TableColumn<>("学号(student_id)");
                studentIdCol.setCellValueFactory(new PropertyValueFactory<>("student_id"));

                TableColumn<RowData, String> nameCol = new TableColumn<>("姓名(name)");
                nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

                TableColumn<RowData, Integer> amountCol = new TableColumn<>("金额(amount)");
                amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

                TableColumn<RowData, String> paymentDateCol = new TableColumn<>("缴费日期(payment_date)");
                paymentDateCol.setCellValueFactory(new PropertyValueFactory<>("payment_date"));

                TableColumn<RowData, String> descriptionCol = new TableColumn<>("描述(description)");
                descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));

                tableView.getColumns().addAll(feeIdCol, studentIdCol, nameCol,amountCol, paymentDateCol);
            }
        }
    }

    public static void populateTable(String tableName, ObservableList<RowData> data, TableView<RowData> tableView, String text) {
        try (Connection connection = Main.getConnection()) {
            ResultSet rs;
            if (tableName.equals("students")|| tableName.equals("dormitories")) {
                if (Objects.equals(text, "")) {
                    String query = "SELECT * FROM " + tableName;
                    Statement stmt = connection.createStatement();
                    rs = stmt.executeQuery(query);
                } else {
                    try {
                        String query = "SELECT * FROM " + tableName + " WHERE " + text;
                        Statement stmt = connection.createStatement();
                        rs = stmt.executeQuery(query);
                        temp = true;
                    } catch (Exception e) {
                        errorPage.create("查询语句出错\n" + e.getMessage());
                        new errorPage().launchErrorPage();
                        temp = false;
                        return;
                    }
                }
            }else{
                if (Objects.equals(text, "")) {
                    String query =  "SELECT * FROM "+tableName;
                    Statement stmt = connection.createStatement();
                    rs = stmt.executeQuery(query);
                } else {
                    try {
                        String query = "SELECT * FROM "+tableName+" where " + text;
                        Statement stmt = connection.createStatement();
                        rs = stmt.executeQuery(query);
                        temp = true;
                    } catch (Exception e) {
                        errorPage.create("查询语句出错\n" + e.getMessage());
                        new errorPage().launchErrorPage();
                        temp = false;
                        return;
                    }
                }
            }
            switch (tableName) {
                case "students":
                    while (rs.next()) {
                        RowData.StudentsRowData rowData = new RowData.StudentsRowData();
                        rowData.setStudent_id(rs.getString("student_id"));
                        rowData.setName(rs.getString("name"));
                        rowData.setGender(rs.getString("gender"));
                        rowData.setMajor(rs.getString("major"));
                        rowData.setClazz(rs.getString("class"));
                        rowData.setContact(rs.getString("contact"));
                        rowData.setEmail(rs.getString("email"));
                        data.add(rowData);
                    }
                    break;
                case "dormitories":
                    while (rs.next()) {
                        RowData.DormitoryRowData rowData = new RowData.DormitoryRowData();
                        rowData.setDormitory_id(rs.getInt("dormitory_id"));
                        rowData.setBuilding_name(rs.getString("building_name"));
                        rowData.setRoom_number(rs.getInt("room_number"));
                        rowData.setCapacity(rs.getInt("capacity"));
                        rowData.setStatus(rs.getString("status"));
                        data.add(rowData);
                    }
                    break;
                case "residences_v":
                    while (rs.next()) {
                        RowData.ResidenceRowData rowData = new RowData.ResidenceRowData();
                        rowData.setResidence_id(rs.getInt("residence_id"));
                        rowData.setStudent_id(rs.getString("student_id"));
                        rowData.setName(rs.getString("name"));
                        rowData.setDormitory_id(rs.getInt("dormitory_id"));
                        rowData.setMove_in_date(rs.getString("move_in_date"));
                        rowData.setMove_out_date(rs.getString("move_out_date"));
                        data.add(rowData);
                    }
                    break;
                case "repairs_v":
                    while (rs.next()) {
                        RowData.RepairRowData rowData = new RowData.RepairRowData();
                        rowData.setRepair_id(rs.getInt("repair_id"));
                        rowData.setStudent_id(rs.getString("student_id"));
                        rowData.setName(rs.getString("name"));
                        rowData.setDormitory_id(rs.getInt("dormitory_id"));
                        rowData.setRequest_date(rs.getString("request_date"));
                        rowData.setDescription(rs.getString("description"));
                        rowData.setStatus(rs.getString("status"));
                        data.add(rowData);
                    }
                    break;
                case "violations_v":
                    while (rs.next()) {
                        RowData.ViolationRowData rowData = new RowData.ViolationRowData();
                        rowData.setViolation_id(rs.getInt("violation_id"));
                        rowData.setStudent_id(rs.getString("student_id"));
                        rowData.setName(rs.getString("name"));
                        rowData.setDormitory_id(rs.getInt("dormitory_id"));
                        rowData.setDate(rs.getString("date"));
                        rowData.setType(rs.getString("type"));
                        rowData.setDetails(rs.getString("details"));
                        data.add(rowData);
                    }
                    break;
                case "fees_v":
                    while (rs.next()) {
                        RowData.FeeRowData rowData = new RowData.FeeRowData();
                        rowData.setFee_id(rs.getString("fee_id"));
                        rowData.setStudent_id(rs.getString("student_id"));
                        rowData.setName(rs.getString("name"));
                        rowData.setAmount(rs.getString("amount"));
                        rowData.setPayment_date(rs.getString("payment_date"));
                        rowData.setDescription(rs.getString("description"));
                        data.add(rowData);
                    }
                    break;
            }
            tableView.setItems(data);
        } catch (Exception e) {
            errorPage.create("展示表格时出错\n" + e.getMessage());
            new errorPage().launchErrorPage();
        }
    }

    @FXML
    public void backBTNClicked() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FromPage));
        Parent root = loader.load();
        Scene scene = backBTN.getScene();
        scene.setRoot(root);
        primaryStage.setScene(scene);
    }

    @FXML
    public void exportBTNClicked() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("export.fxml"));
        Parent root = loader.load();
        Scene scene = exportBTN.getScene();
        scene.setRoot(root);
        primaryStage.setScene(scene);
    }

    @FXML
    public void okBTNClicked() {
        String tableName = switchTableName(tableNameComboBox.getValue());
        ObservableList<RowData> data = FXCollections.observableArrayList();
        // 设置表格列名
        setColumnNames(tableView, tableName);
        // 查询表数据并填充表格
        populateTable(tableName, data, tableView, require.getText());
        if (!temp)
            result.setText("查询结果为空");
        else
            result.setText("查询成功");
    }

    @FXML
    public void initialize() {
        tableNameComboBox.setItems(FXCollections.observableArrayList("学生信息", "宿舍信息", "入住信息", "维修信息", "费用信息", "违规信息"));
        tableNameComboBox.setValue("学生信息");
        require.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                okBTNClicked();
            }
        });
    }
}