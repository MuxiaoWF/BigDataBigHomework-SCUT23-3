package com.muxiao.system;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.sql.*;
import java.util.Arrays;

public class export {
    private static final String PATH = loginEncrypt.basic.desktopPath + "\\宿舍管理系统\\";
    private final Stage primaryStage = Main.primaryStage;
    @FXML
    private Button exportExcelBTN;
    @FXML
    private TextArea result;
    @FXML
    private Button backBTN;
    @FXML
    private Button exportSQLBTN;
    @FXML
    private Button exportXMLBTN;
    @FXML
    private Button exportHTMLBTN;

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
                    "-u " + Main.USER,  // 替换为实际的用户名
                    "-p " + Main.PASSWORD,   // 替换为实际的密码
                    "--result-file=" + PATH + "dormitory.sql",
                    "dormitory"
            };

            // 使用 ProcessBuilder 执行命令
            ProcessBuilder pb = new ProcessBuilder(cmd);
            Process process = pb.start();

            // 读取输出流和错误流
            StringBuilder stdout = new StringBuilder();
            StringBuilder stderr = new StringBuilder();
            Thread stdoutThread = new Thread(() -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stdout.append(line).append("\n");
                    }
                } catch (IOException e) {
                    stderr.append("ERROR: 读取子进程输出流失败\n").append(e.getMessage()).append("\n");
                }
            });

            Thread stderrThread = new Thread(() -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stderr.append(line).append("\n");
                    }
                } catch (IOException e) {
                    stderr.append("ERROR: 读取子进程错误流失败\n").append(e.getMessage()).append("\n");
                }
            });
            stdoutThread.start();
            stderrThread.start();
            // 等待子进程完成
            int exitCode = process.waitFor();
            stdoutThread.join();
            stderrThread.join();
            if (exitCode == 0) {
                return "数据库导出为 SQL 文件成功\n" + stdout;
            } else {
                return "导出错误\n" + stderr + "\n命令: " + Arrays.toString(cmd);
            }
        } catch (Exception e) {
            return "导出错误\n" + e.getMessage();
        }
    }

    public static String exportHTML() {
        try (Connection conn = Main.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});

            BufferedWriter writer = new BufferedWriter(new FileWriter(PATH + "dormitory.html"));
            writer.write("<html><head><title>Database Export</title><style>table {border-collapse: collapse; width: 100%;} th, td {border: 1px solid black; padding: 8px; text-align: left;}</style></head><body>");

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                writer.write("<h2>Table: " + tableName + "</h2>");
                writer.write("<table><thead><tr>");

                ResultSet columns = metaData.getColumns(null, null, tableName, "%");
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    writer.write("<th>" + columnName + "</th>");
                }
                writer.write("</tr></thead><tbody>");

                Statement stmt = conn.createStatement();
                ResultSet data = stmt.executeQuery("SELECT * FROM " + tableName);
                ResultSetMetaData dataMetaData = data.getMetaData();
                int columnCount = dataMetaData.getColumnCount();

                while (data.next()) {
                    writer.write("<tr>");
                    for (int i = 1; i <= columnCount; i++) {
                        String columnValue = data.getString(i);
                        writer.write("<td>" + (columnValue != null ? columnValue : "") + "</td>");
                    }
                    writer.write("</tr>");
                }
                writer.write("</tbody></table><br/>");
            }

            writer.write("</body></html>");
            writer.close();
        } catch (Exception e) {
            return "文件保存失败\n" + e.getMessage();
        }
        return "导出html文件成功!";
    }

    public static String exportXML() {
        try (Connection conn = Main.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("database");
            doc.appendChild(rootElement);

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                Element tableElement = doc.createElement("table");
                tableElement.setAttribute("name", tableName);
                rootElement.appendChild(tableElement);

                ResultSet columns = metaData.getColumns(null, null, tableName, "%");
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    Element columnElement = doc.createElement("column");
                    columnElement.setAttribute("name", columnName);
                    tableElement.appendChild(columnElement);
                }

                Statement stmt = conn.createStatement();
                ResultSet data = stmt.executeQuery("SELECT * FROM " + tableName);
                ResultSetMetaData dataMetaData = data.getMetaData();
                int columnCount = dataMetaData.getColumnCount();

                while (data.next()) {
                    Element rowElement = doc.createElement("row");
                    tableElement.appendChild(rowElement);

                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = dataMetaData.getColumnName(i);
                        String columnValue = data.getString(i);
                        Element valueElement = doc.createElement("value");
                        valueElement.setAttribute("name", columnName);
                        valueElement.setTextContent(columnValue);
                        rowElement.appendChild(valueElement);
                    }
                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(PATH + "dormitory.xml"));
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
        } catch (Exception e) {
            return "文件保存失败\n" + e.getMessage();
        }
        return "导出xml文件成功!";
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
                if (!tableName.equals("dormitories") && !tableName.equals("fees") && !tableName.equals("repairs")
                        && !tableName.equals("residences") && !tableName.equals("students") && !tableName.equals("violations"))
                    break;
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
            try (FileOutputStream outputStream = new FileOutputStream(PATH + "（导出的）数据库原始文件.xlsx")) {
                workbook.write(outputStream);
            }

            return ("数据导出成功");
        } catch (Exception e) {
            return ("导出错误\n" + e);
        }
    }

    @FXML
    private void exportSQLBTNClicked() {
        result.setText(DataBase());
    }

    @FXML
    private void exportXMLBTNClicked() {
        result.setText(exportXML());
    }

    @FXML
    private void exportHTMLBTNClicked() {
        result.setText(exportHTML());
    }

    @FXML
    public void backBTNClicked() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("check.fxml"));
        Parent root = loader.load();
        Scene scene = backBTN.getScene();
        scene.setRoot(root);
        primaryStage.setScene(scene);
    }

    @FXML
    private void exportExcelBTNClicked() {
        result.setText(DataBaseToExcel());
    }
}
