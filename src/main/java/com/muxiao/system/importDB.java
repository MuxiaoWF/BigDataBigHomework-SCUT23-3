package com.muxiao.system;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class importDB {
    public static String importSQL(String path) {
        try {
            // 构建 mysql 命令
            String[] cmd = {
                    "mysql",
                    "-u " + Main.USER,  // 替换为实际的用户名
                    "-p " + Main.PASSWORD,   // 替换为实际的密码
                    "dormitory"
            };
            // 使用 ProcessBuilder 执行命令
            ProcessBuilder pb = new ProcessBuilder(cmd);
            Process process = pb.start();
            // 将 SQL 文件内容写入输入流
            InputStream sqlFileStream = new FileInputStream(path);
            OutputStream processInputStream = process.getOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = sqlFileStream.read(buffer)) > 0) {
                processInputStream.write(buffer, 0, length);
            }
            processInputStream.close();
            sqlFileStream.close();
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
                return "SQL 文件导入成功\n" + stdout;
            } else {
                return "导入错误\n" + stderr + "\n命令: " + Arrays.toString(cmd);
            }
        } catch (Exception e) {
            return "导入错误\n" + e.getMessage();
        }
    }

    public static String importExcel(String path) {
        try (FileInputStream fis = new FileInputStream(path);
             Workbook workbook = new XSSFWorkbook(fis)) {

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String tableName = sheet.getSheetName();

                try (Connection conn = Main.getConnection()) {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SHOW COLUMNS FROM " + tableName);
                    List<String> columnNames = new ArrayList<>();
                    while (rs.next()) {
                        columnNames.add(rs.getString("Field"));
                    }

                    for (Row row : sheet) {
                        if (row.getRowNum() == 0) continue; // 跳过表头

                        StringBuilder insertQuery = new StringBuilder("INSERT INTO " + tableName + " (");
                        StringBuilder values = new StringBuilder("VALUES (");

                        for (int j = 0; j < columnNames.size(); j++) {
                            String columnName = columnNames.get(j);
                            String columnValue = row.getCell(j).getStringCellValue();

                            insertQuery.append(columnName).append(",");
                            values.append("'").append(columnValue).append("',");
                        }

                        insertQuery.deleteCharAt(insertQuery.length() - 1).append(")");
                        values.deleteCharAt(values.length() - 1).append(")");

                        String query = insertQuery + " " + values;
                        stmt.executeUpdate(query);
                    }
                }
            }
            return "Excel 文件导入成功";
        } catch (Exception e) {
            return "导入错误\n" + e.getMessage();
        }
    }

    public static String importHTML(String path) {
        try (Connection conn = Main.getConnection()) {
            File file = new File(path);
            Reader reader = new FileReader(file);

            HTMLEditorKit htmlKit = new HTMLEditorKit();
            HTMLDocument document = (HTMLDocument) htmlKit.createDefaultDocument();
            new ParserDelegator().parse(reader, new HTMLDocumentParser(document), true);

            HTMLDocumentParser parser = new HTMLDocumentParser(document);
            parser.parse();

            List<String> tableNames = new ArrayList<>(parser.getTableNames());
            List<List<String>> columnNamesList = new ArrayList<>(parser.getColumnNamesList());
            List<List<List<String>>> dataLists = new ArrayList<>(parser.getDataLists());

            for (int i = 0; i < tableNames.size(); i++) {
                String tableName = tableNames.get(i);
                List<String> columnNames = columnNamesList.get(i);
                List<List<String>> data = dataLists.get(i);

                for (List<String> row : data) {
                    StringBuilder insertQuery = new StringBuilder("INSERT INTO " + tableName + " (");
                    StringBuilder values = new StringBuilder("VALUES (");

                    for (int j = 0; j < columnNames.size(); j++) {
                        String columnName = columnNames.get(j);
                        String columnValue = row.get(j);

                        insertQuery.append(columnName).append(",");
                        values.append("'").append(columnValue).append("',");
                    }

                    insertQuery.deleteCharAt(insertQuery.length() - 1).append(")");
                    values.deleteCharAt(values.length() - 1).append(")");

                    String query = insertQuery + " " + values;
                    try (Statement stmt = conn.createStatement()) {
                        stmt.executeUpdate(query);
                    }
                }
            }

            return "HTML 文件导入成功";
        } catch (Exception e) {
            return "导入错误\n" + e.getMessage();
        }
    }

    public static String importXML(String path) {
        try (Connection conn = Main.getConnection()) {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(path));

            NodeList tableNodes = doc.getElementsByTagName("table");
            for (int i = 0; i < tableNodes.getLength(); i++) {
                Node tableNode = tableNodes.item(i);
                if (tableNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element tableElement = (Element) tableNode;
                    String tableName = tableElement.getAttribute("name");

                    NodeList rowNodes = tableElement.getElementsByTagName("row");
                    for (int j = 0; j < rowNodes.getLength(); j++) {
                        Node rowNode = rowNodes.item(j);
                        if (rowNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element rowElement = (Element) rowNode;

                            StringBuilder insertQuery = new StringBuilder("INSERT INTO " + tableName + " (");
                            StringBuilder values = new StringBuilder("VALUES (");

                            NodeList valueNodes = rowElement.getElementsByTagName("value");
                            for (int k = 0; k < valueNodes.getLength(); k++) {
                                Node valueNode = valueNodes.item(k);
                                if (valueNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element valueElement = (Element) valueNode;
                                    String columnName = valueElement.getAttribute("name");
                                    String columnValue = valueElement.getTextContent();

                                    insertQuery.append(columnName).append(",");
                                    values.append("'").append(columnValue).append("',");
                                }
                            }
                            insertQuery.deleteCharAt(insertQuery.length() - 1).append(")");
                            values.deleteCharAt(values.length() - 1).append(")");
                            String query = insertQuery + " " + values;
                            try (Statement stmt = conn.createStatement()) {
                                stmt.executeUpdate(query);
                            }
                        }
                    }
                }
            }
            return "XML 文件导入成功";
        } catch (Exception e) {
            return "导入错误\n" + e.getMessage();
        }
    }

    private static class HTMLDocumentParser extends HTMLEditorKit.ParserCallback {
        private final HTMLDocument document;
        private final List<String> tableNames = new ArrayList<>();
        private final List<List<String>> columnNamesList = new ArrayList<>();
        private final List<List<List<String>>> dataLists = new ArrayList<>();
        private String currentTableName = null;
        private List<String> currentColumnNames = null;
        private List<List<String>> currentData = null;

        public HTMLDocumentParser(HTMLDocument document) {
            this.document = document;
        }

        public List<String> getTableNames() {
            return tableNames;
        }

        public List<List<String>> getColumnNamesList() {
            return columnNamesList;
        }

        public List<List<List<String>>> getDataLists() {
            return dataLists;
        }

        @Override
        public void handleStartTag(HTML.Tag tag, MutableAttributeSet attrs, int pos) {
            if (tag == HTML.Tag.TABLE) {
                currentTableName = null;
                currentColumnNames = new ArrayList<>();
                currentData = new ArrayList<>();
            } else if (tag == HTML.Tag.TH) {
                if (currentTableName == null) {
                    Object titleAttr = attrs.getAttribute(HTML.Attribute.TITLE);
                    if (titleAttr != null) {
                        currentTableName = titleAttr.toString();
                        tableNames.add(currentTableName);
                    }
                }
            } else if (tag == HTML.Tag.TR) {
                if (currentColumnNames.isEmpty()) {
                    currentColumnNames = new ArrayList<>();
                } else {
                    currentData.add(new ArrayList<>());
                }
            }
        }

        @Override
        public void handleText(char[] data, int pos) {
            String text = new String(data).trim();
            if (currentTableName != null) {
                if (currentColumnNames.isEmpty()) {
                    currentColumnNames.add(text);
                } else {
                    if (!currentData.isEmpty()) {
                        currentData.get(currentData.size() - 1).add(text);
                    }
                }
            }
        }

        @Override
        public void handleEndTag(HTML.Tag tag, int pos) {
            if (tag == HTML.Tag.TABLE) {
                if (currentTableName != null) {
                    columnNamesList.add(currentColumnNames);
                    dataLists.add(currentData);
                }
            }
        }

        public void parse() {
            try {
                new ParserDelegator().parse(new StringReader(document.getText(0, document.getLength())), this, true);
            } catch (IOException | BadLocationException e) {
                throw new RuntimeException("Error parsing HTML document", e);
            }
        }
    }

}
