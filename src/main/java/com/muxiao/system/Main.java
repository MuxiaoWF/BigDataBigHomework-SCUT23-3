package com.muxiao.system;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main  extends Application {
    public static Stage primaryStage;
    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage=primaryStage;

        File file = new File(PATH);

        // 检查文件是否存在
        if (file.exists()) {
            String s = loginEncrypt.ReversibleEncryption.decrypt(readBinaryData());
            String[] strings = s.split(";;");
            URL= strings[0];
            USER = strings[1];
            PASSWORD = strings[2];
            FXMLLoader loader = new FXMLLoader(getClass().getResource("start.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("宿舍管理系统");
            primaryStage.setScene(new Scene(root, 600, 400));
            primaryStage.show();
            // 在后台线程中执行数据库操作
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(TableCreator::createTables);
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("setDB.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("宿舍管理系统");
            primaryStage.setScene(new Scene(root, 600, 400));
            primaryStage.show();
        }

    }
    public static void main(String[] args) {
        launch(args);
    }

    public static String URL;
    public static String USER;
    public static String PASSWORD;
    private static final String DATABASE_NAME = "DORMITORY";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL + DATABASE_NAME, USER, PASSWORD);
    }
    //创建数据库
    public static void createDatabaseIfNotExists() throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {
            String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            statement.executeUpdate(createDatabaseSQL);
        }
    }
    public static final String PATH = loginEncrypt.basic.desktopPath + "\\宿舍管理系统\\path";
    public static void writeBinaryData(String text) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(PATH);
             DataOutputStream dos = new DataOutputStream(fos)) {

            // 写入字符串长度
            byte[] stringBytes = text.getBytes(StandardCharsets.UTF_8);
            dos.writeInt(stringBytes.length);

            // 写入字符串
            dos.write(stringBytes);
        }
    }

    public static String readBinaryData() throws IOException {
        try (FileInputStream fis = new FileInputStream(PATH);
             DataInputStream dis = new DataInputStream(fis)) {
            // 读取字符串长度
            int length = dis.readInt();
            byte[] stringBytes = new byte[length];

            // 读取字符串
            dis.readFully(stringBytes);
            return new String(stringBytes, StandardCharsets.UTF_8);
        }
    }
//创建表
public static class TableCreator {
    public static void createTables() {
        try {
            Main.createDatabaseIfNotExists();
            try (Connection connection = getConnection()) {
                createStudentsTable(connection);
                createDormitoriesTable(connection);
                createResidencesTable(connection);
                createRepairsTable(connection);
                createViolationsTable(connection);
                createFeesTable(connection);
            } catch (SQLException e) {
                errorPage.create("创建数据表失败\n" + e);
                new errorPage().launchErrorPage();
            }
        } catch (SQLException e) {
            errorPage.create("创建数据库失败\n" + e);
            new errorPage().launchErrorPage();
        }
    }

    private static void createStudentsTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS students (" +
                "student_id CHAR(12) PRIMARY KEY PRIMARY KEY," +
                "name VARCHAR(20) NOT NULL," +
                "gender ENUM('男', '女') NOT NULL," +
                "major VARCHAR(20)," +
                "class VARCHAR(10)," +
                "contact VARCHAR(50)," +
                "email VARCHAR(50)" +
                ")" + "ENGINE=INNODB CHARSET=utf8 COLLATE=utf8_unicode_ci;";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    private static void createDormitoriesTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS dormitories (" +
                "dormitory_id INT PRIMARY KEY," +
                "building_name VARCHAR(5) NOT NULL," +
                "room_number SMALLINT NOT NULL," +
                "capacity TINYINT NOT NULL," +
                "status ENUM('占用', '空') NOT NULL" +
                ")" + "ENGINE=INNODB CHARSET=utf8 COLLATE=utf8_unicode_ci;";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    private static void createResidencesTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS residences (" +
                "residence_id INT AUTO_INCREMENT PRIMARY KEY," +
                "student_id CHAR(12) NOT NULL," +
                "dormitory_id INT NOT NULL," +
                "move_in_date DATE NOT NULL," +
                "move_out_date DATE," +
                "FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE," +
                "FOREIGN KEY (dormitory_id) REFERENCES dormitories(dormitory_id) ON DELETE CASCADE" +
                ")" + "ENGINE=INNODB CHARSET=utf8 COLLATE=utf8_unicode_ci;";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    private static void createRepairsTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS repairs (" +
                "repair_id INT AUTO_INCREMENT PRIMARY KEY," +
                "student_id CHAR(12) NOT NULL," +
                "dormitory_id INT NOT NULL," +
                "request_date DATE NOT NULL," +
                "description TEXT," +
                "status ENUM('待处理', '处理中', '已完成') NOT NULL," +
                "FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE," +
                "FOREIGN KEY (dormitory_id) REFERENCES dormitories(dormitory_id) ON DELETE CASCADE" +
                ")" + "ENGINE=INNODB CHARSET=utf8 COLLATE=utf8_unicode_ci;";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    private static void createViolationsTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS violations (" +
                "violation_id INT AUTO_INCREMENT PRIMARY KEY," +
                "student_id CHAR(12) NOT NULL," +
                "dormitory_id INT NOT NULL," +
                "date DATE NOT NULL," +
                "type VARCHAR(20) NOT NULL," +
                "details TEXT," +
                "FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE," +
                "FOREIGN KEY (dormitory_id) REFERENCES dormitories(dormitory_id) ON DELETE CASCADE" +
                ")" + "ENGINE=INNODB CHARSET=utf8 COLLATE=utf8_unicode_ci;";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    private static void createFeesTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS fees (" +
                "fee_id INT AUTO_INCREMENT PRIMARY KEY," +
                "student_id CHAR(12) NOT NULL," +
                "amount DECIMAL(10, 2) NOT NULL," +
                "payment_date DATE NOT NULL," +
                "description VARCHAR(100)," +
                "FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE" +
                ")" + "ENGINE=INNODB CHARSET=utf8 COLLATE=utf8_unicode_ci;";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }
}

}
