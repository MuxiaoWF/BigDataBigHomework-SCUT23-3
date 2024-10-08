package com.muxiao.system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.sql.*;
import java.util.Arrays;

public class export {
    private static final String PATH = loginEncrypt.basic.desktopPath + "\\宿舍管理系统\\";
    private final Stage primaryStage=Main.primaryStage;
    @FXML
    private Button exportExcelBTN;
    @FXML
    private TextArea result;
    @FXML
    private Button backBTN;
    @FXML
    private Button exportSQLBTN;
    @FXML
    private void exportSQLBTNClicked(ActionEvent event){
        result.setText(DataBase());
    }
    @FXML
    public void backBTNClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("check.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
    }
    @FXML
    private void exportExcelBTNClicked() {
        result.setText(DataBaseToExcel());
    }
    public static String DataBase() {
        try {
            File directory = new File(PATH);
            // 检查并创建目录
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (!created) {
                    return "创建目录失败";
                }
            }
            // 构建 mysqldump 命令
            String[] cmd = {
                    "mysqldump",
                    "-u"+Main.USER,  // 替换为实际的用户名
                    "-p"+Main.PASSWORD,   // 替换为实际的密码
                    "--result-file=" + PATH+"dormitory.sql",
                    "dormitory"
            };

            // 使用 ProcessBuilder 执行命令
            ProcessBuilder pb = new ProcessBuilder(cmd);
            Process process = pb.start();

            // 读取输出流和错误流
            Thread stdoutThread = new Thread(() -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("ERROR: 读取子进程输出流失败");
                }
            });

            Thread stderrThread = new Thread(() -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("ERROR: 读取子进程错误流失败");
                }
            });

            stdoutThread.start();
            stderrThread.start();

            // 等待子进程完成
            int exitCode = process.waitFor();
            stdoutThread.join();
            stderrThread.join();

            if (exitCode == 0) {
                return "数据库导出为 SQL 文件成功";
            } else {
                return "导出错误\n" + Arrays.toString(cmd);
            }
        } catch (Exception e) {
            return "导出错误\n" + e.getMessage();
        }
    }
    public static String DataBaseToExcel() {
        File directory = new File(PATH);
        // 检查并创建目录
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                return "创建目录失败";
            }
        }
        try (Connection connection = Main.getConnection()) {
            connection.setAutoCommit(true);
            // 获取数据库元数据
            DatabaseMetaData metaData = connection.getMetaData();

            // 获取特定 schema 下的所有表的名称
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});

            // 创建Excel工作簿
            Workbook workbook = new XSSFWorkbook();

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("导出中: " + tableName);
                if(!tableName.equals("dormitories")&&!tableName.equals("fees")&&!tableName.equals("repairs")
                        &&!tableName.equals("residences")&&!tableName.equals("students")&&!tableName.equals("violations")) break;
                // 查询表中的数据
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);

                // 创建新的工作表
                Sheet sheet = workbook.createSheet(tableName);

                // 创建表头
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(resultSet.getMetaData().getColumnName(i + 1));
                }

                // 填充数据
                int rowNum = 1;
                while (resultSet.next()) {
                    Row row = sheet.createRow(rowNum++);
                    for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                        Cell cell = row.createCell(i);
                        cell.setCellValue(resultSet.getString(i + 1));
                    }
                }

                // 关闭资源
                resultSet.close();
                statement.close();
            }

            // 写入文件
            try (FileOutputStream outputStream = new FileOutputStream(PATH+"（导出的）数据库原始文件.xlsx")) {
                workbook.write(outputStream);
            }

            return ("数据导出成功");
        } catch (Exception e) {
            return ("导出错误\n"+e);
        }
    }
}
