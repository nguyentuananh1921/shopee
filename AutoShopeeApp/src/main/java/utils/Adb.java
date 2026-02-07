package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static utils.DriverManager.getDriver;

public class Adb {

    public static String sendAdbCommand(String command) {
        StringBuilder result = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec("adb -s " + getDeviceId() + " " + command);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append(System.lineSeparator());
            }

            // Đọc stderr nếu cần (không bắt buộc nhưng tốt)
            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream())
            );
            while ((line = errorReader.readLine()) != null) {
                result.append("ERROR: ").append(line).append(System.lineSeparator());
            }

            if (!process.waitFor(3, TimeUnit.SECONDS)) {
                process.destroy();
                System.err.println("Lệnh adb bị treo, đã hủy tiến trình.");
            }

            System.out.println("[ADB Command] " + command + " => Device: " + getDeviceId());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }

        return result.toString().trim();
    }

    public static void back(){
        sendAdbCommand("shell input keyevent 4");
    }
    public static void tap(int x, int y){
        sendAdbCommand("shell input tap " + x + " " + y);
    }
    public static void paste(){
        sendAdbCommand("shell input keyevent KEYCODE_PASTE");
    }
    public static void navigateToLink(String link){
        sendAdbCommand("shell am start -a android.intent.action.VIEW -d " + link);
    }
    public static String getSimSerial() {
        try {
            Process process = Runtime.getRuntime().exec("adb -s " + getDeviceId() +" shell service call iphonesubinfo 10");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder simSerial = new StringBuilder();
            // Regex bắt đoạn giữa dấu nháy đơn
            Pattern pattern = Pattern.compile("'([^']*)'");
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    simSerial.append(matcher.group(1)); // Lấy phần giữa dấu '
                }
            }
            process.waitFor();
            // Bỏ dấu chấm để có số gốc
            String cleaned = simSerial.toString().replace(".", "").trim();
            System.out.println("adb : " + cleaned);
            return cleaned;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getManufacturer(){
        return sendAdbCommand("shell getprop ro.product.manufacturer");
    }

    public static  void swipe(int startX, int startY, int endX, int endY, int durationMillis){
        sendAdbCommand("shell input swipe " + startX +" " + startY + " " + endX + " " + endY + " " + durationMillis);
    }

    public static String getDeviceId() {
        try {
            Object udid = getDriver().getCapabilities().getCapability("udid");
            return udid != null ? udid.toString() : "Unknown";
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi khi lấy deviceId: " + e.getMessage());
            return "Unknown";
        }
    }

    public static  boolean checkCurrentApp(String packageName){
        String line = "";
        try {
            String cmd = "adb -s " + getDeviceId() + " shell dumpsys window | findstr mFocusedWindow" ;
            ProcessBuilder builder = new ProcessBuilder("cmd", "/c", cmd);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );
            while ((line = reader.readLine()) != null) {
                if (line.contains(packageName)) {
                    System.out.println("App đang mở trên devices " + getDeviceId().toUpperCase() + " : " + line.trim());
                    break;
                }
            }

            if (!process.waitFor(3, TimeUnit.SECONDS)) {
                process.destroy();
                System.err.println("Lệnh adb bị treo, đã hủy tiến trình.");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return line != null && line.contains(packageName);
    }

    public static  String getCurrentApp(String packageName){
        String line = "";
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "adb", "-s", getDeviceId(), "shell", "dumpsys", "window", "|", "mFocusedWindow"
            );
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            while ((line = reader.readLine()) != null) {
                if (line.contains(packageName)) {
                    System.out.println("App đang mở trên devices " + getDeviceId().toUpperCase() + " : " + line.trim());
                    break;
                }
            }

            process.waitFor();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return line;
    }
    public static  void goToNovelahApp(){
        sendAdbCommand("shell monkey -p com.novel.novelah -c android.intent.category.LAUNCHER 1");
        MobileUI.sleep(10);
    }

    public static  void grantStoragePermission(String packageName){
        sendAdbCommand("shell pm grant " + packageName + " android.permission.READ_EXTERNAL_STORAGE");
        sendAdbCommand("shell pm grant " + packageName + " android.permission.WRITE_EXTERNAL_STORAGE");
    }

    public void revokeReadStoragePermission(String packageName){
        sendAdbCommand("shell pm revoke " + packageName + " android.permission.READ_EXTERNAL_STORAGE");
    }
    public void revokeWriteStoragePermission(String packageName){
        sendAdbCommand("shell pm revoke " + packageName + " android.permission.WRITE_EXTERNAL_STORAGE");
    }
    public void grantPstNotificationPermission(String packageName){
        //Commands chỉ hoạt động trên sdk 33 trở lên tương đương android 13
        sendAdbCommand("shell pm grant "+ packageName +" android.permission.POST_NOTIFICATIONS");
    }
    public static String runSystemCommand(String command) {
        StringBuilder result = new StringBuilder();
        try {
            Process process = new ProcessBuilder("cmd.exe", "/c", command).start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append(System.lineSeparator());
            }

            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream())
            );
            while ((line = errorReader.readLine()) != null) {
                result.append("ERROR: ").append(line).append(System.lineSeparator());
            }

            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }

        return result.toString().trim();
    }

    public static String getClipboardText() {

        String result = sendAdbCommand("shell am broadcast -n ch.pete.adbclipboard/.ReadReceiver");

        Pattern pattern = Pattern.compile("data=\"(.*)\"");
        Matcher matcher = pattern.matcher(result);
        if (matcher.find()) {
            return matcher.group(1);
        }
        else return null;

    }

}
