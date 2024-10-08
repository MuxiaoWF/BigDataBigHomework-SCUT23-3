package com.muxiao.system;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class loginEncrypt {
    private static String username;
    private static String password;
    private static final String PATH = basic.desktopPath + "\\宿舍管理系统\\user.txt";
    public static String inputUsername;
    private static ArrayList<String> usernameList = getUsernameList();
    private static ArrayList<String> getUsernameList() {
        ArrayList<String> usernameList = new ArrayList<>();
        String[] strArr = basic.readText(PATH);
        for (int i = 0; i < strArr.length; i++) {
            if (i % 2 == 1) {
                username = strArr[i - 1];
                usernameList.add(basic.decrypt(username));
            }
        }
        return usernameList;
    }
    public static void register(String username, String password) {
        usernameList = getUsernameList();
        loginEncrypt.username = username;
        loginEncrypt.password = password;
        String[] users = usernameList.toArray(String[]::new);
        if(basic.check_same(users, username))
            throw new RuntimeException("用户名重复");
        password = basic.encrypt_SHA256(username+password);
        username = basic.encrypt(username);
        try {
            basic.write(PATH, username);
        } catch (IOException e) {
            throw new RuntimeException("ERROR:写入文件失败！请关闭所有有关的文件（user.txt, xlsx等）");
        }
        try {
            basic.write(PATH, password);
        } catch (IOException e) {
            throw new RuntimeException("ERROR:写入文件失败！请关闭所有有关的文件（user.txt, xlsx等）");
        }
    }
    private static int turnTime = 3;
    public static boolean login(String inputUsername, String inputPassword) {
        if(inputUsername.isEmpty()){
            errorPage.create("输入用户名！");
            new errorPage().launchErrorPage();
            return false;
        }else if(inputPassword.isEmpty()){
            errorPage.create("输入密码！");
            new errorPage().launchErrorPage();
            return false;
        }
        String[] strArr = basic.readText(PATH);
        usernameList = getUsernameList();
        // 创建个ArrayList
        ArrayList<String> passwordList = new ArrayList<>();
        for (int i = 0; i < strArr.length; i++) {
            if (i % 2 == 1) {
                password = strArr[i];
                passwordList.add(password);
            }
        }
        int result = 3;
        loginEncrypt.inputUsername = inputUsername;
        if (usernameList.isEmpty()) {
            errorPage.create("没有任何一个用户！");
            new errorPage().launchErrorPage();
            return false;
        }
        for (int i = 0; i < usernameList.size(); i++) {
            if (inputUsername.equals(usernameList.get(i))&&
                    basic.decrypt_SHA256(inputUsername+inputPassword, passwordList.get(i))) {
                result = 1;
                break;
            } else if (turnTime == 1) {
                result = 0;
            } else {
                result = 2;
            }
        }
        if (result == 1) {
            return true;
        } else if (result == 2) {
            errorPage.create("用户名或密码错误\n" + "You have " + (turnTime - 1) + " attempts left.");
            new errorPage().launchErrorPage();
            turnTime = turnTime - 1;
            System.out.println();
            return false;
        }else {
            errorPage.create("你已经没有机会了！");
            new errorPage().launchErrorPage();
            Main.primaryStage.close();
            return false;
        }
    }
    public static class basic {
        //写入文件
        public static final Path desktopPath = getDesktopPath();
        public static void write(String filename, String text) throws IOException {
            //新建文件夹
            File file = new File(filename);
            File path = new File(file.getParent());
            if (!path.exists()) {
                if (!path.mkdirs()) {
                    throw new RuntimeException("ERROR:创建文件夹  " + path+" 失败");
                }
            }
            try (FileWriter writer = new FileWriter(filename, true)) {
                writer.write(text + "\n");
            }
        }
        private static Path getDesktopPath() {
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.contains("windows")) {
                return Paths.get(System.getenv("USERPROFILE"), "Desktop");
            } else if (osName.contains("mac") || osName.contains("darwin")) {
                return Paths.get(System.getProperty("user.home"), "Desktop");
            } else if (osName.contains("linux") || osName.contains("unix")) {
                String xdgDesktop = System.getenv("XDG_DESKTOP_DIR");
                if (xdgDesktop != null && !xdgDesktop.isEmpty()) {
                    return Paths.get(xdgDesktop);
                } else {
                    return Paths.get(System.getProperty("user.home"), "Desktop");
                }
            } else {
                throw new RuntimeException("Unsupported operating system: " + osName);
            }
        }
        public static void file_exist(String filename) throws IOException {
            if (!new File(filename).exists()) {
                write(filename, "\n");
            }
        }
        //按每行读取文件
        public static String [] readText(String filename) {
            boolean temp = true;
            while (temp){
                try {
                    file_exist(filename);
                    temp = false;
                } catch (IOException e) {
                    throw new RuntimeException("ERROR:读取  " + filename +"文件失败！请先自行检查是否有问题");
                }
            }

            temp = true;
            String str = "";
            while (temp){
                try (FileReader reader = new FileReader(filename)) {
                    char[] buffer = new char[2048];
                    int len = reader.read(buffer);
                    str = new String(buffer, 0, len);
                    temp = false;
                } catch (IOException e) {
                    throw new RuntimeException("ERROR: 读取  " + filename +"文件失败！请先自行检查是否有问题");
                }
            }
            return str.split("\n");
        }
        //单个替换字符，指定位置
        public static String replaceCharAtPosition(String text, int position, char replacementChar) {
            if (position >= 0 && position < text.length()) {
                StringBuilder sb = new StringBuilder(text);
                sb.setCharAt(position, replacementChar);
                return sb.toString();
            } else {
                throw new IndexOutOfBoundsException("ERROR: 系统错误，无法正常替换");
            }
        }
        //解密方法
        public static String decrypt(String text) {
            int num = text.length();
            char[] char_list = text.toCharArray();
            ArrayList<String> words = new ArrayList<>();
            for (int i = 0; i < num; i++) {
                if (i % 2 == 0) {
                    words.add(String.valueOf(char_list[i]));
                }
            }

            StringBuilder string = new StringBuilder();
            for (String word : words) {
                string.append(word);
            }
            String joinedWords = string.toString();

            for (int i = 0; i < joinedWords.length(); i++) {
                char c = joinedWords.charAt(i);
                int decryptText = 158 - (int) c ;
                joinedWords = replaceCharAtPosition(joinedWords,i, (char) decryptText);
            }
            return joinedWords;
        }
        public static String encrypt_SHA256(String input)  {
            MessageDigest sha256;
            try {
                sha256 = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            input = "Muxiao_Wanfeng+" + input;
            byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
            sha256.update(inputBytes);

            byte[] hashBytes = sha256.digest();

            BigInteger bigInt = new BigInteger(1, hashBytes);
            StringBuilder hexString = new StringBuilder(bigInt.toString(16));

            while (hexString.length() < 64) {
                hexString.insert(0, "0");
            }
            return hexString.toString();
        }

        public static boolean decrypt_SHA256(String input, String hash) {
            input = encrypt_SHA256(input);
            return input.equals(hash);
        }
        public static boolean check_same(String[] items_arr,String add_items) {
            Set<String> set = new HashSet<>();
            set.add(add_items);
            if (items_arr != null) {
                for (String s : items_arr) {
                    if (!set.add(s)) {
                        errorPage.create("存在重复的名称：" + s);
                        new errorPage().launchErrorPage();
                        return true;
                    }
                }
            }
            return false;
        }
        public static String encrypt(String text) {
            int num = text.length();
            char[] char_list = text.toCharArray();
            ArrayList<String> words = new ArrayList<>();
            for (int i = 0; i < num; i++) {
                words.add(String.valueOf(char_list[i]));
                words.add(String.valueOf((char)((int)(Math.random() *95 + 32))));
            }

            StringBuilder string = new StringBuilder();
            for (String word : words) {
                string.append(word);
            }
            String joinedWords = string.toString();

            for (int i = 0; i < joinedWords.length(); i++) {
                char c = joinedWords.charAt(i);
                int encryptText = 158 - (int) c ;
                joinedWords = replaceCharAtPosition(joinedWords,i, (char) encryptText);  //不能直接replace因为44会循环一次就变成jj
            }
            return joinedWords;
        }
    }
    public static class ReversibleEncryption {

        private static final String ALGORITHM = "AES";
        private static final String KEY = "Mu_Xiao_Wan_Feng";

        public static String encrypt(String data) throws Exception {
            SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedData = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedData);
        }

        public static String decrypt(String encryptedData) throws Exception {
            SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedData);
        }

        public static void main(String[] args) throws Exception {
            String originalData = "Hello, World!";
            System.out.println("原始数据：" + originalData);

            String encryptedData = encrypt(originalData);
            System.out.println("加密后的数据：" + encryptedData);

            String decryptedData = decrypt(encryptedData);
            System.out.println("解密后的数据：" + decryptedData);
        }
    }
}
