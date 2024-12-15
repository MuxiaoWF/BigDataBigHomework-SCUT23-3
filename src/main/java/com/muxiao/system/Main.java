package com.muxiao.system;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application {
    public static final String PATH = loginEncrypt.basic.desktopPath + "\\宿舍管理系统\\path";
    private static final String DATABASE_NAME = "DORMITORY";
    public static Stage primaryStage;
    public static String URL;
    public static String USER;
    public static String PASSWORD;

    public static void main(String[] args) {
        launch(args);
    }

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

    public static void writeBinaryData(String PATH, String text, boolean append) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(PATH, append);
             DataOutputStream dos = new DataOutputStream(fos)) {
            // 写入字符串长度
            byte[] stringBytes = text.getBytes(StandardCharsets.UTF_8);
            dos.writeInt(stringBytes.length);
            // 写入字符串
            dos.write(stringBytes);
        }
    }


    public static String readBinaryData(String PATH) throws IOException {
        List<String> result = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(PATH);
             DataInputStream dis = new DataInputStream(fis)) {

            while (dis.available() > 0) {
                try {
                    // 读取字符串长度
                    int length = dis.readInt();
                    byte[] stringBytes = new byte[length];

                    // 读取字符串
                    dis.readFully(stringBytes);
                    result.add(new String(stringBytes, StandardCharsets.UTF_8));
                } catch (EOFException e) {
                    // 文件结束，跳出循环
                    break;
                }
            }
        }
        StringBuilder string = new StringBuilder();
        for (String s : result) {
            string.append(s);
        }
        return string.toString();
    }

    private static void createView(Connection connection) throws SQLException {
        String[] views = {
                "residences_v",
                "fees_v",
                "repairs_v",
                "violations_v"
        };

        for (String view : views) {
            try (Statement statement = connection.createStatement()) {
                // 检查视图是否存在
                String checkViewSQL = "SHOW TABLES LIKE '" + view + "'";
                ResultSet resultSet = statement.executeQuery(checkViewSQL);
                if (!resultSet.next()) {
                    // 视图不存在，创建视图
                    String createViewSQL = getCreateViewSQL(view);
                    statement.executeUpdate(createViewSQL);
                }
            }
        }
    }

    private static String getCreateViewSQL(String view) {
        return switch (view) {
            case "residences_v" -> "CREATE VIEW residences_v AS " +
                    "SELECT r.*, s.name AS name FROM residences r JOIN students s ON r.student_id = s.student_id;";
            case "fees_v" -> "CREATE VIEW fees_v AS " +
                    "SELECT r.*, s.name AS name FROM fees r JOIN students s ON r.student_id = s.student_id;";
            case "repairs_v" -> "CREATE VIEW repairs_v AS " +
                    "SELECT r.*, s.name AS name FROM repairs r JOIN students s ON r.student_id = s.student_id;";
            case "violations_v" -> "CREATE VIEW violations_v AS " +
                    "SELECT r.*, s.name AS name FROM violations r JOIN students s ON r.student_id = s.student_id;";
            default -> throw new IllegalArgumentException("Unknown view: " + view);
        };
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;
        File file = new File(PATH);
        // 检查文件是否存在
        if (file.exists()) {
            String s = loginEncrypt.ReversibleEncryption.decrypt(readBinaryData(PATH));
            String[] strings = s.split(";_OwO_;");
            URL = strings[0];
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
            try (Connection connection = getConnection()){
                TableCreator.createUser(connection);
            }catch (SQLException e) {
                errorPage.create("数据库无法连接！（检查数据库开启状态）"+e);
                new errorPage().launchErrorPage();
                loader = new FXMLLoader(getClass().getResource("setDB.fxml"));
                root = loader.load();
                primaryStage.setTitle("宿舍管理系统");
                primaryStage.setScene(new Scene(root, 600, 400));
                primaryStage.show();
            }
            USER = "MuxiaoWF";
            PASSWORD = "";
            File user = new File(loginEncrypt.basic.desktopPath + "\\宿舍管理系统\\user");
            if(!user.exists()){
                try (Statement stmt = getConnection().createStatement()){
                    ResultSet rs = stmt.executeQuery("select user from mysql.user");
                    while (rs.next()) {
                        if (rs.getString("user").contains("_muxiao"))
                            stmt.executeQuery("drop user '" + rs.getString("user") + "'");
                    }
                }catch (SQLException e){
                    errorPage.create("程序出错哦！检查user是否被删除（若删除path一起删了）"+e);
                    new errorPage().launchErrorPage();
                }
            }
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("setDB.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("宿舍管理系统");
            primaryStage.setScene(new Scene(root, 600, 400));
            primaryStage.show();
        }
    }

    //创建表
    public static class TableCreator {
        public static void createTables() {
            try {
                Main.createDatabaseIfNotExists();
                try (Connection connection = getConnection()) {
                    Main.createView(connection);
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

        private static void createUser(Connection connection) throws SQLException {
            String sql = "CREATE USER IF NOT EXISTS 'MuxiaoWF'@'%' IDENTIFIED BY '';";
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
                statement.executeUpdate("GRANT SELECT ON dormitory.* TO 'MuxiaoWF'@'%';");
                statement.executeUpdate("FLUSH  PRIVILEGES ;");
            }
            Main.USER = "MuxiaoWF";
            Main.PASSWORD = "";
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
