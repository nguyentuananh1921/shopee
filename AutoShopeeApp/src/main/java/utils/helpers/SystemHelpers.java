package utils.helpers;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SystemHelpers {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    Random random;
    public static String makeSlug(String input) {
        if (input == null)
            throw new IllegalArgumentException();

        String noWhiteSpace = WHITESPACE.matcher(input).replaceAll("_");
        String normalized = Normalizer.normalize(noWhiteSpace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }
    public static int randomInt(int a, int b){
        Random random = new Random();
        return random.nextInt(a,b+1);
    }

    public static void executeCommand(String command)
            throws IOException, InterruptedException {

        ProcessBuilder processBuilder;

        if (isWindows()) {
            processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
        } else {
            processBuilder = new ProcessBuilder("bash", "-c", command);
        }

        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        process.waitFor();
    }


    public static void restartServer() {
        try {
            // Kill ADB server
            System.out.println("Killing ADB server...");
            executeCommand("adb kill-server");

            // Start ADB server
            System.out.println("Starting ADB server...");
            executeCommand("adb start-server");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void connectDevice(String device) {
        try {
            // Kill ADB server
            System.out.println("Connectting device " + device + " ...");
            executeCommand("adb connect " + device);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void connectDevice(int index) {
        try {
            // Kill ADB server
            String device = "127.0.0.1:"+(21503 + index * 10);
            System.out.println("Connectting device " + device + " ...");
            executeCommand("adb connect " + device);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return Get the path to your source directory with a / at the end
     */
    public static String getCurrentDir() {
        String current = System.getProperty("user.dir") + File.separator;
        return current;
    }

    /**
     * Create folder empty
     *
     * @param path path to create folder
     */
    public static void createFolder(String path) {
        // File is a class inside java.io package
        File file = new File(path);

        String result = null;

        int lengthSum = path.length();
        int lengthSub = path.substring(0, path.lastIndexOf('/')).length();

        result = path.substring(lengthSub, lengthSum);

        if (!file.exists()) {
            file.mkdir();  // mkdir is used to create folder
            System.out.println("Folder " + file.getName() + " created: " + path);
        } else {
            System.out.println("Folder already created");
        }
    }

    /**
     * @param str        string to be split based on condition
     * @param valueSplit the character to split the string into an array of values
     * @return array of string values after splitting
     */
    public static ArrayList<String> splitString(String str, String valueSplit) {
        ArrayList<String> arrayListString = new ArrayList<>();
        for (String s : str.split(valueSplit, 0)) {
            arrayListString.add(s);
        }
        return arrayListString;
    }

    public static boolean checkValueInListString(String expected, String listValues[]) {
        boolean found = false;

        for (String s : listValues) {
            if (s.equals(expected)) {
                found = true;
                break;
            }
        }
        return found;
    }

    public static boolean checkValueInListString(String expected, List<String> listValues) {
        boolean found = false;

        for (String s : listValues) {
            if (s.equals(expected)) {
                found = true;
                break;
            }
        }
        return found;
    }
    public static void adbRestartServer(){
        ProcessBuilder kill = new ProcessBuilder(
                "adb",
                "kill-server"
        );
        ProcessBuilder start = new ProcessBuilder(
                "adb",
                "start-server"
        );
        try {
            kill.start();
            start.start();
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }
    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }
    public static void killPort(String port) {
        try {
            Process process;

            if (isWindows()) {
                process = new ProcessBuilder(
                        "cmd.exe", "/c",
                        "for /f \"tokens=5\" %a in ('netstat -ano ^| findstr :" + port + "') do taskkill /PID %a /F"
                ).start();
            } else {
                process = new ProcessBuilder(
                        "bash", "-c",
                        "lsof -ti tcp:" + port + " | xargs kill -9"
                ).start();
            }

            process.waitFor();
            System.out.println("Checked and killed process on port: " + port);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void killProcessOnPort(String port) {
        String command = "";

        // Check OS to set command to find and kill process
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            command = "cmd /c netstat -ano | findstr :" + port;
        } else {
            command = "lsof -i :" + port;
        }

        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.trim().split("\\s+");
                String pid = tokens[1];  // PID position may vary by OS
                System.out.println(pid);
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    Runtime.getRuntime().exec("taskkill /PID " + pid +" /F");
                } else {
                    Runtime.getRuntime().exec("kill -9 " + pid);
                }
            }
            reader.close();
            process.waitFor();
            System.out.println("####### Kill process on port " + port + " successfully.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void startAppiumWithPlugins(String server, String port) {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "appium",
                "-a", server,
                "-p", port,
                "-ka", "800",
                "--use-plugins", "appium-reporter-plugin,element-wait,gestures,device-farm,appium-dashboard",
                "-pa", "/",
                "--plugin-device-farm-platform", "android"
        );

        // Redirect error and output streams
        processBuilder.redirectErrorStream(true);

        try {
            // Start the process
            Process process = processBuilder.start();
            System.out.println("Appium server started with plugins.");

            // Optional: Read the output (if needed for debugging)
            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> runAdminCommandAndReadOutput(String rawCommand) {
        List<String> outputLines = new ArrayList<>();

        try {
            // Tạo file tạm để ghi kết quả
            Path tempOutputFile = Files.createTempFile("admin_cmd_output_", ".txt");
            String outputFilePath = tempOutputFile.toAbsolutePath().toString();

            // Ghép lệnh với chuyển hướng output vào file
            String cmdWithRedirect = String.format("'%s > \\\"%s\\\"'", rawCommand, outputFilePath);

            // Lệnh chạy powershell với quyền admin và ghi file
            String powershellCmd = String.format(
                    "Start-Process cmd.exe -ArgumentList '/c %s' -Verb RunAs -Wait", cmdWithRedirect
            );

            ProcessBuilder builder = new ProcessBuilder("powershell", "-Command", powershellCmd);
            builder.inheritIO(); // Cho hiện cửa sổ UAC nếu cần
            Process process = builder.start();
            process.waitFor();

            // Đọc từng dòng từ file
            try (BufferedReader reader = new BufferedReader(new FileReader(outputFilePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    outputLines.add(line);
                }
            }

            // Xóa file tạm
            Files.deleteIfExists(tempOutputFile);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return outputLines;
    }

    public static String getCurrentDate(){
        LocalDate today = LocalDate.now();
        return today.toString();
    }


    public static String getRandomMacAddress() {
        Random rand = new Random();
        StringBuilder mac = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            int value = rand.nextInt(256); // 0-255
            mac.append(String.format("%02X", value));
            if (i != 5) {
                mac.append(":");
            }
        }
        return mac.toString();
    }
    public static String getRandomIMEI() {
        StringBuilder imei = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < 15; i++) {
            imei.append(rand.nextInt(10)); // 0-9
        }
        return imei.toString();
    }

    public static String getRandomAndroidID() {
        Random rand = new Random();
        StringBuilder androidId = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            androidId.append(Integer.toHexString(rand.nextInt(16)));
        }
        return androidId.toString();
    }

    public static String getRandomIMSI() {
        StringBuilder imsi = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < 15; i++) {
            imsi.append(rand.nextInt(10));
        }
        return imsi.toString();
    }

    public static String getRandomSimSerial() {
        StringBuilder simSerial = new StringBuilder();
        Random rand = new Random();
        int length = 19 + rand.nextInt(2); // 19 hoặc 20 số
        for (int i = 0; i < length; i++) {
            simSerial.append(rand.nextInt(10));
        }
        return simSerial.toString();
    }

    public static String getRandomPhoneNumber() {
        Random rand = new Random();
        String[] prefixes = {"09", "03", "08", "07", "05"};
        StringBuilder phone = new StringBuilder();
        phone.append(prefixes[rand.nextInt(prefixes.length)]);
        for (int i = phone.length(); i < 10; i++) {
            phone.append(rand.nextInt(10));
        }
        return phone.toString();
    }

    public static String getRandomManufacturer() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for(int i = 0; i < 10; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }

        return sb.toString();
    }

    public static String getRandomModel() {
        String[] models = {"SM-G973F", "Pixel 6", "SM-A526B", "Redmi Note 10", "V2031","N-1001","VS-S7G5"};
        Random rand = new Random();
        return models[rand.nextInt(models.length)];
    }

    public static String getRandomBrand() {
        String[] mans = {"samsung", "google", "xiaomi", "vivo", "oppo","itel","nokia","honda","yamaha","suzuki","apple","realme"};
        Random rand = new Random();
        return mans[rand.nextInt(mans.length)];
    }


    public static String generateSimSerial() {
        String prefix = "898403"; // 8984 = ICCID prefix, 03 = mã nhà mạng phụ
        int length = 20; // ICCID thường dài 19-20 chữ số

        int randomLength = length - prefix.length();

        StringBuilder sb = new StringBuilder(prefix);
        Random rand = new Random();

        for (int i = 0; i < randomLength; i++) {
            int digit = rand.nextInt(10); // Tạo số từ 0-9
            sb.append(digit);
        }

        return sb.toString();
    }

    public static boolean isImageOnScreen(String screenPath, String templatePath, double threshold) {
        Mat img = Imgcodecs.imread(screenPath);
        Mat template = Imgcodecs.imread(templatePath);

        int resultCols = img.cols() - template.cols() + 1;
        int resultRows = img.rows() - template.rows() + 1;
        Mat result = new Mat(resultRows, resultCols, CvType.CV_32FC1);

        Imgproc.matchTemplate(img, template, result, Imgproc.TM_CCOEFF_NORMED);
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

        // Nếu giá trị maxVal >= threshold => tìm thấy ảnh
        return mmr.maxVal >= threshold;
    }
    public static Point findImageOnScreen(String screenPath, String templatePath, double threshold) {
        Mat img = Imgcodecs.imread(screenPath);
        Mat template = Imgcodecs.imread(templatePath);

        int resultCols = img.cols() - template.cols() + 1;
        int resultRows = img.rows() - template.rows() + 1;
        Mat result = new Mat(resultRows, resultCols, CvType.CV_32FC1);

        Imgproc.matchTemplate(img, template, result, Imgproc.TM_CCOEFF_NORMED);
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

        // Nếu match vượt ngưỡng thì trả về tọa độ, ngược lại trả về null
        if (mmr.maxVal >= threshold) {
            return mmr.maxLoc; // Trả về điểm góc trên bên trái của vị trí tìm thấy
        } else {
            return null;
        }
    }


    public static Document parseXml(String pageSource){
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new ByteArrayInputStream(pageSource.getBytes()));
        }
        catch (IOException e){
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
        return null;
    }




    private static final Set<String> AXES = Set.of(
            "ancestor","ancestor-or-self","attribute","child","descendant",
            "descendant-or-self","following","following-sibling","namespace",
            "parent","preceding","preceding-sibling","self"
    );

    public static String normalizeXPath(String raw) {
        if (raw == null || raw.isEmpty()) return raw;

        StringBuilder out = new StringBuilder();
        int i = 0;
        while (i < raw.length()) {
            // giữ nguyên // hoặc /
            if (raw.startsWith("//", i)) { out.append("//"); i += 2; continue; }
            if (raw.charAt(i) == '/')   { out.append('/');  i += 1; continue; }

            // Lấy một "step" đến dấu '/' kế tiếp (bên ngoài predicate)
            int start = i, depth = 0;
            while (i < raw.length()) {
                char c = raw.charAt(i);
                if (c == '[') depth++;
                else if (c == ']') depth--;
                else if (c == '/' && depth == 0) break;
                i++;
            }
            String step = raw.substring(start, i);
            out.append(normalizeStep(step));
        }
        return out.toString();
    }

    private static String normalizeStep(String step) {
        step = step.trim();
        if (step.isEmpty()) return "";

        // Các trường hợp đặc biệt: ., .., *, node(), text(), comment(), processing-instruction()
        if (step.equals(".") || step.equals("..") || step.equals("*") ||
                step.endsWith("()")) {
            return step;
        }

        // Tách axis nếu có
        String axis = null;
        String rest = step;
        int idx = step.indexOf("::");
        if (idx > 0) {
            String maybeAxis = step.substring(0, idx);
            if (AXES.contains(maybeAxis)) {
                axis = maybeAxis;
                rest = step.substring(idx + 2);
            }
        }

        // Tách nodeName và predicates (phần trong []), KHÔNG đụng vào predicate
        String nodeName;
        String predicates = "";
        int pIdx = rest.indexOf('[');
        if (pIdx >= 0) {
            nodeName = rest.substring(0, pIdx);
            predicates = rest.substring(pIdx); // giữ nguyên y chang, không replace nháy
        } else {
            nodeName = rest;
        }
        nodeName = nodeName.trim();

        // Nếu là wildcard hoặc function thì giữ nguyên
        String nodeTest;
        if (nodeName.equals("*") || nodeName.endsWith("()") || nodeName.isEmpty()) {
            nodeTest = nodeName.isEmpty() ? "*" : nodeName;
        } else {
            // Bọc bằng local-name() với NHÁY KÉP để tránh đụng nháy đơn trong predicate
            nodeTest = "*[local-name()=\"" + nodeName + "\"]";
        }

        if (axis != null) {
            return axis + "::" + nodeTest + predicates;
        } else {
            return nodeTest + predicates;
        }
    }

    public static int getNumberInText(String text){

        // Regex: "Xem video \\+ (\\d+) đồng"
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            String amount = matcher.group(1);  // Nhóm 1 là phần số
            System.out.println("Số tiền nhận thưởng được: " + amount);
            return Integer.parseInt(amount);
        } else {
            System.out.println("Không tìm thấy số.");
            return 0;
        }
    }
    public static List<String> getConnectedDevices() {
        List<String> devices = new ArrayList<>();

        try {
            Process process = Runtime.getRuntime().exec("adb devices");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.endsWith("\tdevice") && !line.startsWith("List")) {
                    devices.add(line.split("\t")[0]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return devices;
    }

}