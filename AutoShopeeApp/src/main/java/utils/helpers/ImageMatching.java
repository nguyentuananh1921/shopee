package utils.helpers;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import utils.Adb;
import utils.MobileUI;

public class ImageMatching {
    private static final Object lock = new Object();
    static {
        System.load(SystemHelpers.getCurrentDir() + "\\opencv_java451.dll");
    }

    public static boolean isImageOnScreen(String templatePath, double threshold) {
        try {
            Mat img = Imgcodecs.imread(MobileUI.captureScreenshot().getAbsolutePath());
            Mat template = Imgcodecs.imread(templatePath);

            int resultCols = img.cols() - template.cols() + 1;
            int resultRows = img.rows() - template.rows() + 1;
            Mat result = new Mat(resultRows, resultCols, CvType.CV_32FC1);

            Imgproc.matchTemplate(img, template, result, Imgproc.TM_SQDIFF);
            Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
            System.out.println("Độ khác nhau giữa 2 images : " + mmr.minVal );
            // Với TM_SQDIFF => match tốt là minVal nhỏ hơn threshold
            return mmr.minVal <= threshold;
        }
        catch (Exception e) {
            return false;
        }
    }
    public static boolean isImageOnScreen(String screen, String templatePath, double threshold) {
        try {
            Mat img = Imgcodecs.imread(screen);
            Mat template = Imgcodecs.imread(templatePath);

            int resultCols = img.cols() - template.cols() + 1;
            int resultRows = img.rows() - template.rows() + 1;
            Mat result = new Mat(resultRows, resultCols, CvType.CV_32FC1);

            Imgproc.matchTemplate(img, template, result, Imgproc.TM_SQDIFF);
            Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

            // Nếu giá trị maxVal >= threshold => tìm thấy ảnh
            return mmr.maxVal >= threshold;
        }
        catch (Exception e){
            return false;
        }

    }

    public static Point getPointImageOnScreen(String screenPath, String templatePath, double threshold) {
        Mat img = Imgcodecs.imread(screenPath);
        Mat template = Imgcodecs.imread(templatePath);

        int resultCols = img.cols() - template.cols() + 1;
        int resultRows = img.rows() - template.rows() + 1;
        Mat result = new Mat(resultRows, resultCols, CvType.CV_32FC1);

        Imgproc.matchTemplate(img, template, result, Imgproc.TM_SQDIFF);  // sử dụng TM_SQDIFF
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

        // Với TM_SQDIFF, match tốt là khi minVal nhỏ hơn threshold
        if (mmr.minVal <= threshold) {
            Point topLeft = mmr.minLoc;
            double centerX = topLeft.x + template.cols() / 2.0;
            double centerY = topLeft.y + template.rows() / 2.0;
            return new Point(centerX, centerY);

        } else {
            return null;
        }
    }

    public static boolean findAndClickImageOnScreen(String image) {
        synchronized (lock) {
            try {
                Point point = getPointImageOnScreen(MobileUI.captureScreenshot().getAbsolutePath(), SystemHelpers.getCurrentDir() + "\\images\\" + image, 0.1);
                if (point != null) {
                    Adb.tap((int) point.x, (int) point.y);
                    System.out.println("Image " + image + " is on the screen!");
                    return true;
                } else {
                    System.out.println("Image " + image + " is NOT on the screen.");
                    return false;
                }
            } catch (Exception e) {
                System.out.println("Can't compare Image " + image + " on the screen.");
                return false;
            }
        }
    }
    public static boolean waitForImageOnscreen(String image, int timeout) {
        int attempts = 0;
        while (!isImageOnScreen(image, 0.1) && attempts < timeout) {
            MobileUI.sleep(1);
            System.out.println("chưa tìm thấy " + image + " trên màn hình");
            attempts++;
        }
        // Nếu tìm thấy thì trả về true, nếu không thì trả về false
        return isImageOnScreen(image, 0.1);
    }


}
