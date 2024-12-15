package com.muxiao.system;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static com.muxiao.system.Main.readBinaryData;

public class loginEncrypt {
    private static final String PATH = basic.desktopPath + "\\宿舍管理系统\\user";
    public static String inputUsername;
    private static String username;
    private static String password;
    private static ArrayList<String> usernameList = getUsernameList();
    private static int turnTime = 3;

    private static ArrayList<String> getUsernameList() {
        ArrayList<String> usernameList = new ArrayList<>();
        String[] strArr = null;
        if (!new File(PATH).exists())
            return usernameList;
        try {
            strArr = readBinaryData(PATH).split("\n");
        } catch (IOException e) {
            errorPage.create("用户文件出错！（尝试手动删除user文件）" + e);
            new errorPage().launchErrorPage();
        } catch (NullPointerException e) {
            return usernameList;
        }
        if (strArr != null) {
            for (int i = 0; i < strArr.length; i++) {
                if (i % 2 == 1) {
                    username = strArr[i - 1];
                    usernameList.add(basic.decrypt(username));
                }
            }
        }
        return usernameList;
    }

    public static void deleteUser(String username) {
        usernameList = getUsernameList();
        List<String> passwordList = getPasswordList();
        String[] users = usernameList.toArray(String[]::new);
        if (basic.check_same(users, username,false)) {
            int index = Arrays.asList(users).indexOf(username);
            usernameList.remove(index);
            passwordList.remove(index);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < usernameList.size(); i++) {
                sb.append(basic.encrypt(usernameList.get(i))).append("\n");
                sb.append(basic.encrypt(passwordList.get(i))).append("\n");
            }
            try {
                Main.writeBinaryData(PATH, sb.toString(), false);
            } catch (IOException e) {
                errorPage.create("ERROR:写入文件失败！请关闭所有有关的文件（user, xlsx等）" + e);
                new errorPage().launchErrorPage();
            }
        }
    }

    private static List<String> getPasswordList() {
        List<String> passwordList = new ArrayList<>();
        String[] strArr = null;
        if (!new File(PATH).exists())
            return passwordList;
        try {
            strArr = readBinaryData(PATH).split("\n");
        } catch (IOException e) {
            errorPage.create("用户文件出错！（尝试手动删除user文件）" + e);
            new errorPage().launchErrorPage();
        } catch (NullPointerException e) {
            return passwordList;
        }
        if (strArr != null) {
            for (int i = 0; i < strArr.length; i++) {
                if (i % 2 == 0) {
                    passwordList.add(strArr[i + 1]);
                }
            }
        }
        return passwordList;
    }

    public static void register(String username, String password) {
        usernameList = getUsernameList();
        loginEncrypt.username = username;
        loginEncrypt.password = password;
        String[] users = usernameList.toArray(String[]::new);
        if (basic.check_same(users, username,true))
            throw new RuntimeException("用户名重复");
        password = basic.encrypt_SHA256(username + password);
        username = basic.encrypt(username);
        try {
            Main.writeBinaryData(PATH, username + "\n" + password + "\n", true);
        } catch (IOException e) {
            throw new RuntimeException("ERROR:写入文件失败！请关闭所有有关的文件（user, xlsx等）" + e);
        } catch (NullPointerException ignored) {
        }
    }

    public static boolean loginAdmin(String inputUsername, String inputPassword) {
        if (inputUsername.isEmpty()) {
            errorPage.create("输入用户名！");
            new errorPage().launchErrorPage();
            return false;
        } else if (inputPassword.isEmpty()) {
            errorPage.create("输入密码！");
            new errorPage().launchErrorPage();
            return false;
        }
        String s = null;
        try {
            s = ReversibleEncryption.decrypt(readBinaryData(Main.PATH));
        } catch (Exception e) {
            errorPage.create("获取管理员用户名和密码出错！");
            new errorPage().launchErrorPage();
        }
        String[] strings = s.split(";_OwO_;");
        if (inputPassword.equals(strings[2]) && inputUsername.equals(strings[1])) {
            Main.USER = strings[1];
            Main.PASSWORD = strings[2];
            turnTime = 3;
            return true;
        } else {
            turnTime = turnTime - 1;
            errorPage.create("用户名或密码错误\n你还有" + turnTime + "次机会");
            new errorPage().launchErrorPage();
            if (turnTime == 0) {
                loginAdmin.stage.close();
                login.isAdmin = false;
                turnTime = 3;
            }
            return false;
        }
    }

    public static boolean login(String inputUsername, String inputPassword) {
        if (inputUsername.isEmpty()) {
            errorPage.create("输入用户名！");
            new errorPage().launchErrorPage();
            return false;
        } else if (inputPassword.isEmpty()) {
            errorPage.create("输入密码！");
            new errorPage().launchErrorPage();
            return false;
        }
        String[] strArr = null;
        try {
            strArr = readBinaryData(PATH).split("\n");
        } catch (IOException e) {
            errorPage.create("用户文件出错！(尝试手动删除user文件)");
            new errorPage().launchErrorPage();
        }
        usernameList = getUsernameList();
        // 创建个ArrayList
        ArrayList<String> passwordList = new ArrayList<>();
        if (strArr != null) {
            for (int i = 0; i < strArr.length; i++) {
                if (i % 2 == 1) {
                    password = strArr[i];
                    passwordList.add(password);
                }
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
            if (inputUsername.equals(usernameList.get(i)) &&
                    basic.decrypt_SHA256(inputUsername + inputPassword, passwordList.get(i))) {
                Main.USER = inputUsername + "_muxiao";
                Main.PASSWORD = inputPassword;
                try {
                    if (inputUsername.equals(ReversibleEncryption.decrypt(readBinaryData(Main.PATH)).split(";_OwO_;")[1])) {
                        login.isAdmin = true;
                        Main.USER = inputUsername;
                    }
                } catch (Exception e) {
                    errorPage.create("出错！" + e);
                    new errorPage().launchErrorPage();
                }
                result = 1;
                turnTime = 3;
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
            return false;
        } else {
            errorPage.create("你已经没有机会了！");
            new errorPage().launchErrorPage();
            Main.primaryStage.close();
            return false;
        }
    }

    public static class basic {
        //写入文件
        public static final Path desktopPath = getDesktopPath();

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
                int decryptText = 158 - (int) c;
                joinedWords = replaceCharAtPosition(joinedWords, i, (char) decryptText);
            }
            return joinedWords;
        }

        public static String encrypt_SHA256(String input) {
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

        public static boolean check_same(String[] items_arr, String add_items, boolean b) {
            Set<String> set = new HashSet<>();
            set.add(add_items);
            if (items_arr != null) {
                for (String s : items_arr) {
                    if (!set.add(s) && b) {
                        errorPage.create("存在重复的名称：" + s);
                        new errorPage().launchErrorPage();
                        return true;
                    } else if (!set.add(s) && !b)
                        return true;
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
                words.add(String.valueOf((char) ((int) (Math.random() * 95 + 32))));
            }

            StringBuilder string = new StringBuilder();
            for (String word : words) {
                string.append(word);
            }
            String joinedWords = string.toString();

            for (int i = 0; i < joinedWords.length(); i++) {
                char c = joinedWords.charAt(i);
                int encryptText = 158 - (int) c;
                joinedWords = replaceCharAtPosition(joinedWords, i, (char) encryptText);  //不能直接replace因为44会循环一次就变成jj
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
    }
}
