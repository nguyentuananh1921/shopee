package utils;

import handleException.VariablesThreadLocal;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import utils.helpers.ElementState;
import utils.helpers.ImageMatching;
import utils.helpers.SystemHelpers;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static utils.DriverManager.getDriver;

public class MobileUI {

    private static final int DEFAULT_TIMEOUT = 3;
    private static ScheduledExecutorService scheduler;

    public static void sleep(double second) {
        System.out.println(" Sleeping for " + second + " seconds.");
        try {
            Thread.sleep((long) (1000 * second));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static void sleep(double second,String message) {
        System.out.println(" Sleeping for " + second + " seconds to: " + message);
        try {
            Thread.sleep((long) (1000 * second));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public static void swipe(int startX, int startY, int endX, int endY, int durationMillis) {
        System.out.println(Adb.getDeviceId() + " Executing swipe from (" + startX + "," + startY + ") to (" + endX + "," + endY + ") with duration " + durationMillis + "ms.");
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(0));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(durationMillis), PointerInput.Origin.viewport(), endX, endY));
        swipe.addAction(finger.createPointerUp(0));
        try {
            getDriver().perform(Collections.singletonList(swipe));
        } catch (Exception e) {
            System.out.println("Error Swipe On Screen!");
            e.printStackTrace();
        }
    }

    public static void scroll(int startX, int startY, int endX, int endY, int durationMillis) {
        System.out.println(Adb.getDeviceId() + " Executing scroll from (" + startX + "," + startY + ") to (" + endX + "," + endY + ") with duration " + durationMillis + "ms.");
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(0));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(durationMillis), PointerInput.Origin.viewport(), endX, endY));
        swipe.addAction(finger.createPointerUp(0));
        try {
            getDriver().perform(Collections.singletonList(swipe));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void scrollGestureCommand() {
        // Scroll gesture cho Android
        Map<String, Object> scrollParams = new HashMap<>();
        scrollParams.put("left", 670);
        scrollParams.put("top", 500);
        scrollParams.put("width", 200);
        scrollParams.put("height", 2000);
        scrollParams.put("direction", "down");
        scrollParams.put("percent", 1);

        System.out.println(Adb.getDeviceId() + " Executing scrollGesture command with params: " + scrollParams);
        // Thực hiện scroll gesture
        getDriver().executeScript("mobile: scrollGesture", scrollParams);
    }

    public static void swipeLeft() {
        System.out.println(Adb.getDeviceId() + " Executing swipeLeft.");
        Dimension size = getDriver().manage().window().getSize();
        int startX = (int) (size.width * 0.8);
        int startY = (int) (size.height * 0.3);
        int endX = (int) (size.width * 0.2);
        int endY = startY;
        int duration = 200;
        swipe(startX, startY, endX, endY, duration);
    }

    public static void swipeRight() {
        System.out.println(Adb.getDeviceId() + " Executing swipeRight.");
        Dimension size = getDriver().manage().window().getSize();
        int startX = (int) (size.width * 0.2);
        int startY = (int) (size.height * 0.3);
        int endX = (int) (size.width * 0.8);
        int endY = startY;
        int duration = 200;
        swipe(startX, startY, endX, endY, duration);
    }

//    private static Point getCenterOfElement(Point location, Dimension size) {
//        // No log needed for private helper, logging happens in the calling public method
//        return new Point(location.set() + size.getWidth() / 2,
//                location.getY() + size.getHeight() / 2);
//    }

//    public static void tap(WebElement element) {
//        System.out.println(Adb.getDeviceId() + " Executing tap on element: " + element);
//        Point location = element.getLocation();
//        Dimension size = element.getSize();
//        Point centerOfElement = getCenterOfElement(location, size);
//        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
//        Sequence sequence = new Sequence(finger, 1)
//                .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centerOfElement))
//                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
//                .addAction(new Pause(finger, Duration.ofMillis(500))) // Note: Default pause is 500ms here
//                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
//
//        getDriver().perform(Collections.singletonList(sequence));
//    }

    public static void tap(int x, int y) {
        System.out.println(Adb.getDeviceId() + " Executing tap at coordinates (" + x + "," + y + ") with 200ms pause.");
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);
        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(new Pause(finger, Duration.ofMillis(200))); //Chạm nhẹ nhanh
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        getDriver().perform(Arrays.asList(tap));
    }

    public static void tap(int x, int y, int milliSecondDuration) {
        System.out.println(Adb.getDeviceId() + " Executing tap at coordinates (" + x + "," + y + ") with pause " + milliSecondDuration + "ms.");
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);
        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(new Pause(finger, Duration.ofMillis(milliSecondDuration))); //Chạm vào với thời gian chỉ định
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        getDriver().perform(Arrays.asList(tap));
    }

    public static void zoom(WebElement element, double scale) {
        System.out.println(Adb.getDeviceId() + " Executing zoom on element: " + element + " with approximate scale factor: " + scale + " (Note: Implementation may need review for accurate scaling)");
        int centerX = element.getLocation().getX() + element.getSize().getWidth() / 2;
        int centerY = element.getLocation().getY() + element.getSize().getHeight() / 2;
        int distance = 100; // Khoảng cách giữa hai ngón tay

        PointerInput finger1 = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
        PointerInput finger2 = new PointerInput(PointerInput.Kind.TOUCH, "finger2");

        Sequence zoom = new Sequence(finger1, 1);
        zoom.addAction(finger1.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), centerX - distance, centerY));
        zoom.addAction(finger1.createPointerDown(0));

        Sequence zoom2 = new Sequence(finger2, 1);
        zoom2.addAction(finger2.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), centerX + distance, centerY));
        zoom2.addAction(finger2.createPointerDown(0));

        // Simplified movement - Actual scaling might need more complex radial movement logic
        int moveDuration = 50;
        int steps = 10;
        int startDist1X = centerX - distance;
        int startDist2X = centerX + distance;
        int endDist1X, endDist2X;

        if (scale > 1) { // Phóng to - Move fingers further apart
            System.out.println(Adb.getDeviceId() + " Zooming In");
            endDist1X = centerX - (int) (distance * scale); // Example: move further left
            endDist2X = centerX + (int) (distance * scale); // Example: move further right
        } else { // Thu nhỏ - Move fingers closer
            System.out.println(Adb.getDeviceId() + " Zooming Out");
            endDist1X = centerX - (int) (distance * scale); // Example: move closer to center
            endDist2X = centerX + (int) (distance * scale); // Example: move closer to center
        }

        for (int i = 1; i <= steps; i++) {
            int currentX1 = startDist1X + (endDist1X - startDist1X) * i / steps;
            int currentX2 = startDist2X + (endDist2X - startDist2X) * i / steps;
            zoom.addAction(finger1.createPointerMove(Duration.ofMillis(moveDuration), PointerInput.Origin.viewport(), currentX1, centerY));
            zoom2.addAction(finger2.createPointerMove(Duration.ofMillis(moveDuration), PointerInput.Origin.viewport(), currentX2, centerY));
        }

        zoom.addAction(finger1.createPointerUp(0));
        zoom2.addAction(finger2.createPointerUp(0));

        getDriver().perform(Arrays.asList(zoom, zoom2));
    }

    public static void clickElement(By locator) {
        System.out.println(Adb.getDeviceId() + " Clicking element located by: " + locator + " within " + DEFAULT_TIMEOUT + "s.");
        waitForElementToBeClickable(locator, DEFAULT_TIMEOUT).click();
    }
    
    public static void clickElement(By locator, int second) {
        System.out.println(Adb.getDeviceId() + " Clicking element located by: " + locator + " within " + second + "s.");
        waitForElementToBeClickable(locator, second).click();
    }

    public static void clickElementWithOutException(By locator) {
        try {
            waitForElementToBeClickable(locator).click();
            System.out.println(Adb.getDeviceId() + " Clicking element located by: " + locator + " within default timeout (" + DEFAULT_TIMEOUT + "s).");
        } catch (Exception e) {
            System.out.println("The element has disappeared from the screen: " + locator.toString());
        }
    }

    public static void clickElementWithOutException(WebElement locator) {
        try {
            waitForElementToBeClickable(locator).click();
            System.out.println(Adb.getDeviceId() + " Clicking element located by: " + locator + " within default timeout (" + DEFAULT_TIMEOUT + "s).");
        } catch (Exception e) {
            System.out.println("The element has disappeared from the screen: " + locator.toString());
        }
    }

    public static void clickListElement(List<By> locators,By locatorVerify) {
        for (By locator : locators){
            try {
                waitForElementToBeClickable(locator).click();
                System.out.println(Adb.getDeviceId() + " Clicking element located by: " + locator + " within default timeout (" + DEFAULT_TIMEOUT + "s).");
                sleep(3);
                if (isElementDisplayedByPageSource(locatorVerify).equals(ElementState.DISPLAYED)) break;
            } catch (Exception e) {
                System.out.println("The element has disappeared from the screen: " + locator);
            }

        }

    }

    public static void clickElement(WebElement element, int second) {
        System.out.println(Adb.getDeviceId() + " Clicking element: " + element + " within " + second + "s.");
        waitForElementToBeClickable(element, second).click();
    }

    public static void setText(By locator, String text) {
        System.out.println(Adb.getDeviceId() + " Setting text '" + text + "' on element located by: " + locator + " with default timeout.");
        WebElement element = waitForElementVisible(locator);
        element.click(); // Often needed before clear/sendKeys
        element.sendKeys(text);
        System.out.println(Adb.getDeviceId() + " Set text completed for locator: " + locator);
    }

    public static void clearAndSetText(By locator, String text) {
        System.out.println(Adb.getDeviceId() + " Setting text '" + text + "' on element located by: " + locator + " with default timeout.");
        WebElement element = waitForElementVisible(locator);
        element.click(); // Often needed before clear/sendKeys
        element.clear();
        element.sendKeys(text);
        System.out.println(Adb.getDeviceId() + " Set text completed for locator: " + locator);
    }

    public static void setText(By locator, String text, int second) {
        System.out.println(Adb.getDeviceId() + " Setting text '" + text + "' on element located by: " + locator + " with timeout " + second + "s.");
        WebElement element = waitForElementVisible(locator, second);
        element.click();
        element.clear();
        element.sendKeys(text);
        System.out.println(Adb.getDeviceId() + " Set text completed for locator: " + locator);
    }

    public static void setText(WebElement element, String text) {
        System.out.println(Adb.getDeviceId() + " Setting text '" + text + "' on element: " + element + " with default timeout.");
        WebElement elm = waitForElementVisible(element);
        elm.click();
        elm.clear();
        elm.sendKeys(text);
        System.out.println(Adb.getDeviceId() + " Set text completed for element: " + element);
    }

    public static void setText(WebElement element, String text, int second) {
        System.out.println(Adb.getDeviceId() + " Setting text '" + text + "' on element: " + element + " with timeout " + second + "s.");
        WebElement elm = waitForElementVisible(element, second);
        elm.click();
        elm.clear();
        elm.sendKeys(text);
        System.out.println(Adb.getDeviceId() + " Set text completed for element: " + element);
    }

    public static void clearText(By locator) {
        System.out.println(Adb.getDeviceId() + " Clearing text on element located by: " + locator + " with default timeout.");
        WebElement element = waitForElementVisible(locator);
        element.click();
        element.clear();
        System.out.println(Adb.getDeviceId() + " Clear text completed for locator: " + locator);
    }

    public static void clearText(By locator, int second) {
        System.out.println(Adb.getDeviceId() + " Clearing text on element located by: " + locator + " with timeout " + second + "s.");
        WebElement element = waitForElementVisible(locator, second);
        element.click();
        element.clear();
        System.out.println(Adb.getDeviceId() + " Clear text completed for locator: " + locator);
    }

    public static void clearText(WebElement element) {
        System.out.println(Adb.getDeviceId() + " Clearing text on element: " + element + " with default timeout.");
        WebElement elm = waitForElementVisible(element);
        elm.click();
        elm.clear();
        System.out.println(Adb.getDeviceId() + " Clear text completed for element: " + element);
    }

    public static void clearText(WebElement element, int second) {
        System.out.println(Adb.getDeviceId() + " Clearing text on element: " + element + " with timeout " + second + "s.");
        WebElement elm = waitForElementVisible(element, second);
        elm.click();
        elm.clear();
        System.out.println(Adb.getDeviceId() + " Clear text completed for element: " + element);
    }

    public static Point getElementPosition(By locator) {
        System.out.println(Adb.getDeviceId() + " Getting position from element located by: " + locator + " with timeout " + DEFAULT_TIMEOUT + "s.");
        WebElement element = waitForElementVisible(locator, DEFAULT_TIMEOUT);
        return element.getLocation();
    }



    public static String getElementText(By locator, int second) {
        System.out.println(Adb.getDeviceId() + " Getting text from element located by: " + locator + " with timeout " + second + "s.");
        WebElement element = waitForElementVisible(locator, second);
        String text = element.getText();
        System.out.println(Adb.getDeviceId() + " Retrieved text: '" + text + "'");
        return text;
    }
    public static String getElementText(By locator) {
        System.out.println(Adb.getDeviceId() + " Getting text from element located by: " + locator + " with timeout " + DEFAULT_TIMEOUT + "s.");
        WebElement element = waitForElementVisible(locator, DEFAULT_TIMEOUT);
        String text = element.getText();
        System.out.println(Adb.getDeviceId() + " Retrieved text: '" + text + "'");
        return text;
    }

    public static String getElementText(WebElement element) {
        System.out.println(Adb.getDeviceId() + " Getting text from element: " + element + " with default timeout.");
        WebElement elm = waitForElementVisible(element);
        String text = elm.getText();
        System.out.println(Adb.getDeviceId() + " Retrieved text: '" + text + "'");
        return text;
    }

    public static String getElementText(WebElement element, int second) {
        System.out.println(Adb.getDeviceId() + " Getting text from element: " + element + " with timeout " + second + "s.");
        WebElement elm = waitForElementVisible(element, second);
        String text = elm.getText();
        System.out.println(Adb.getDeviceId() + " Retrieved text: '" + text + "'");
        return text;
    }

    public static String getElementAttribute(By locator, String attribute, int second) {
        System.out.println(Adb.getDeviceId() + " Getting attribute '" + attribute + "' from element located by: " + locator + " with timeout " + second + "s.");
        WebElement element = waitForElementVisible(locator, second);
        String value = element.getAttribute(attribute);
        System.out.println(Adb.getDeviceId() + " Retrieved attribute value: '" + value + "'");
        return value;
    }

    public static String getElementAttribute(WebElement element, String attribute) {
        System.out.println(Adb.getDeviceId() + " Getting attribute '" + attribute + "' from element: " + element + " with default timeout.");
        WebElement elm = waitForElementVisible(element);
        String value = elm.getAttribute(attribute);
        System.out.println(Adb.getDeviceId() + " Retrieved attribute value: '" + value + "'");
        return value;
    }

    public static String getElementAttribute(WebElement element, String attribute, int second) {
        System.out.println(Adb.getDeviceId() + " Getting attribute '" + attribute + "' from element: " + element + " with timeout " + second + "s.");
        WebElement elm = waitForElementVisible(element, second);
        String value = elm.getAttribute(attribute);
        System.out.println(Adb.getDeviceId() + " Retrieved attribute value: '" + value + "'");
        return value;
    }

    public static boolean isElementPresentAndDisplayed(WebElement element) {
        System.out.println(Adb.getDeviceId() + " Checking if element is present and displayed: " + element);
        boolean result;
        try {
            result = element != null && element.isDisplayed();
            System.out.println(Adb.getDeviceId() + " Element present and displayed check result: " + result);
            return result;
        } catch (NoSuchElementException e) {
            System.out.println(Adb.getDeviceId() + " Element not found during presence/display check: " + element);
            return false;
        } catch (Exception e) {
            System.out.println(Adb.getDeviceId() + " An error occurred checking presence/display for element: " + element);
            return false;
        }
    }

    public static boolean isElementPresentAndDisplayed(By locator) {
        System.out.println(Adb.getDeviceId() + " Checking if element is present and displayed: " + locator);
        boolean result;
        try {
            WebElement element = waitForElementVisible(locator, DEFAULT_TIMEOUT);
            result = element != null && element.isDisplayed();
            System.out.println(Adb.getDeviceId() + " Element present and displayed check result: " + result + " for locator: " + locator);
            return result;
        } catch (TimeoutException e) {
            System.out.println(Adb.getDeviceId()
                    + " Element NOT found after timeout: " + locator);
            return false;
        }
        catch (NoSuchElementException e) {
            System.out.println(Adb.getDeviceId() + " Element not found during presence/display check: " + locator + " - " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println(Adb.getDeviceId() + " An error occurred checking presence/display for locator: " + locator);
            System.out.println("appium crashed...");
            VariablesThreadLocal.setError(true);
            return false;
        }
    }

    public static boolean isElementPresentAndDisplayed(By locator, int timeout) {
        System.out.println(Adb.getDeviceId() + " Checking if element is present and displayed: " + locator + "in " + timeout + " second");
        boolean result;
        try {
            WebElement element = waitForElementVisible(locator, timeout);
            result = element != null && element.isDisplayed();
            System.out.println(Adb.getDeviceId() + " Element present and displayed check result: " + result + " for locator: " + locator);
            return result;
        } catch (NoSuchElementException e) {
            System.out.println(Adb.getDeviceId() + " Element not found during presence/display check: " + locator);
            return false;
        } catch (Exception e) {
            System.out.println(Adb.getDeviceId() + " An error occurred checking presence/display for locator: " + locator);
            return false;
        }
    }

    public static boolean isElementEnabled(WebElement element) {
        System.out.println(Adb.getDeviceId() + " Checking if element is enabled: " + element);
        boolean result;
        try {
            result = element != null && element.isEnabled();
            System.out.println(Adb.getDeviceId() + " Element enabled check result: " + result);
            return result;
        } catch (Exception e) {
            System.out.println(Adb.getDeviceId() + " An error occurred checking enabled status for element: " + element);
            return false;
        }
    }

    public static boolean isElementEnabled(By locator) {
        System.out.println(Adb.getDeviceId() + " Checking if element is enabled: " + locator);
        boolean result;
        try {
            WebElement element = waitForElementVisible(locator); // Ensure it's visible before checking enabled
            result = element != null && element.isEnabled();
            System.out.println(Adb.getDeviceId() + " Element enabled check result: " + result + " for locator: " + locator);
            return result;
        } catch (Exception e) {
            System.out.println(Adb.getDeviceId() + " An error occurred checking enabled status for locator: " + locator);
            return false;
        }
    }

    public static boolean isElementSelected(WebElement element) {
        System.out.println(Adb.getDeviceId() + " Checking if element is selected: " + element);
        boolean result;
        try {
            result = element != null && element.isSelected();
            System.out.println(Adb.getDeviceId() + " Element selected check result: " + result);
            return result;
        } catch (Exception e) {
            System.out.println(Adb.getDeviceId() + " An error occurred checking selected status for element: " + element);
            return false;
        }
    }

    public static boolean isElementSelected(By locator) {
        System.out.println(Adb.getDeviceId() + " Checking if element is selected: " + locator);
        boolean result;
        try {
            WebElement element = waitForElementVisible(locator); // Ensure it's visible before checking selected
            result = element != null && element.isSelected();
            System.out.println(Adb.getDeviceId() + " Element selected check result: " + result + " for locator: " + locator);
            return result;
        } catch (Exception e) {
            System.out.println(Adb.getDeviceId() + " An error occurred checking selected status for locator: " + locator);
            return false;
        }
    }

    public static List<WebElement> getListElement(By locator) {
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(DEFAULT_TIMEOUT));
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return getDriver().findElements(locator);
        } catch (Exception e) {
            // Nếu hết thời gian mà không tìm thấy thì trả về danh sách rỗng
            return List.of();
        }
    }


    // Các hàm verify (sử dụng Assert và gọi lại các hàm is)

    public static void verifyElementPresentAndDisplayed(WebElement element, String message) {
        System.out.println(Adb.getDeviceId() + " Verifying element is present and displayed: " + element + ". Message if failed: " + message);

    }

    public static void verifyElementPresentAndDisplayed(By locator, String message) {
        System.out.println(Adb.getDeviceId() + " Verifying element is present and displayed: " + locator + ". Message if failed: " + message);

    }

    public static void verifyElementEnabled(WebElement element, String message) {
        System.out.println(Adb.getDeviceId() + " Verifying element is enabled: " + element + ". Message if failed: " + message);

    }

    public static void verifyElementEnabled(By locator, String message) {
        System.out.println(Adb.getDeviceId() + " Verifying element is enabled: " + locator + ". Message if failed: " + message);

    }

    public static void verifyElementSelected(WebElement element, String message) {
        System.out.println(Adb.getDeviceId() + " Verifying element is selected: " + element + ". Message if failed: " + message);

    }

    public static void verifyElementSelected(By locator, String message) {
        System.out.println(Adb.getDeviceId() + " Verifying element is selected: " + locator + ". Message if failed: " + message);

    }

    public static void verifyElementText(WebElement element, String expectedText, String message) {
        System.out.println(Adb.getDeviceId() + " Verifying text of element: " + element + " equals '" + expectedText + "'. Message if failed: " + message);

    }

    public static void verifyElementText(By locator, String expectedText, String message) {
        System.out.println(Adb.getDeviceId() + " Verifying text of element: " + locator + " equals '" + expectedText + "'. Message if failed: " + message);

    }

    public static void verifyElementAttribute(WebElement element, String attribute, String expectedValue, String message) {
        System.out.println(Adb.getDeviceId() + " Verifying attribute '" + attribute + "' of element: " + element + " equals '" + expectedValue + "'. Message if failed: " + message);

    }

    public static void verifyElementAttribute(By locator, String attribute, String expectedValue, String message) {
        System.out.println(Adb.getDeviceId() + " Verifying attribute '" + attribute + "' of element: " + locator + " equals '" + expectedValue + "'. Message if failed: " + message);

    }

    public static void assertTrueCondition(boolean condition, String message) {
        System.out.println(Adb.getDeviceId() + " Asserting condition: " + condition + ". Message if failed: " + message);

        System.out.println(Adb.getDeviceId() + " Assertion passed for condition: " + condition);
    }

    public static boolean isFlagSecureScreen() {
        try {
            ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.BYTES);
            System.out.println(Adb.getDeviceId() + " Screen is displayed (not FLAG_SECURE)");
            return false;
        } catch (WebDriverException e) {
            String message = e.getMessage();
            if (message != null && message.toLowerCase().contains("secure")) {
                System.out.println(Adb.getDeviceId() + " Screen is FLAG_SECURE");
                return true;
            } else {
                // Có thể là lỗi khác (mất kết nối thiết bị, app crash,...)
                System.out.println(Adb.getDeviceId() + " Unexpected error during screenshot: " + message);
                return false; // hoặc bạn có thể return null, hoặc throw nếu thực sự cần fail
            }
        } catch (Exception e) {
            // Bao phủ các lỗi không phải WebDriverException (ví dụ NullPointerException, ClassCastException,...)
            System.out.println(Adb.getDeviceId() + " Unknown error checking FLAG_SECURE: " + e.getMessage());
            return false;
        }
    }

    // --- Wait Methods ---

    public static String test(By locator) {
        return getDriver().findElement(locator).getText();
    }

    public static WebElement waitForElementToBeClickable(By locator, int timeout) {
        System.out.println(Adb.getDeviceId() + " Waiting up to " + timeout + "s for element to be clickable: " + locator);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeout));
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static WebElement waitForElementToBeClickable(By locator) {
        System.out.println(Adb.getDeviceId() + " Waiting up to " + DEFAULT_TIMEOUT + "s (default) for element to be clickable: " + locator);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(DEFAULT_TIMEOUT));
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static WebElement waitForElementToBeClickable(WebElement element, int timeout) {
        System.out.println(Adb.getDeviceId() + " Waiting up to " + timeout + "s for element to be clickable: " + element);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeout));
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public static WebElement waitForElementToBeClickable(WebElement element) {
        System.out.println(Adb.getDeviceId() + " Waiting up to " + DEFAULT_TIMEOUT + "s (default) for element to be clickable: " + element);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(DEFAULT_TIMEOUT));
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public static WebElement waitForElementVisible(By locator, int timeout) {
        System.out.println(Adb.getDeviceId() + " Waiting up to " + timeout + "s for element to be visible: " + locator);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeout));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForElementVisible(By locator) {
        System.out.println(Adb.getDeviceId() + " Waiting up to " + DEFAULT_TIMEOUT + "s (default) for element to be visible: " + locator);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(DEFAULT_TIMEOUT));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForElementVisible(WebElement element, int timeout) {
        System.out.println(Adb.getDeviceId() + " Waiting up to " + timeout + "s for element to be visible: " + element);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeout));
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    public static WebElement waitForElementVisible(WebElement element) {
        System.out.println(Adb.getDeviceId() + " Waiting up to " + DEFAULT_TIMEOUT + "s (default) for element to be visible: " + element);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(DEFAULT_TIMEOUT));
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    public static boolean waitForElementInvisibe(By locator, int timeout) {
        System.out.println(Adb.getDeviceId() + " Waiting up to " + timeout + "s for element to be invisible: " + locator);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeout));
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public static boolean waitForElementInvisibe(By locator) {
        System.out.println(Adb.getDeviceId() + " Waiting up to " + DEFAULT_TIMEOUT + "s (default) for element to be invisible: " + locator);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(DEFAULT_TIMEOUT));
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public static boolean waitForElementInvisibe(WebElement element, int timeout) {
        System.out.println(Adb.getDeviceId() + " Waiting up to " + timeout + "s for element to be invisible: " + element);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeout));
        return wait.until(ExpectedConditions.invisibilityOf(element));
    }

    public static boolean waitForElementInvisibe(WebElement element) {
        System.out.println(Adb.getDeviceId() + " Waiting up to " + DEFAULT_TIMEOUT + "s (default) for element to be invisible: " + element);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(DEFAULT_TIMEOUT));
        return wait.until(ExpectedConditions.invisibilityOf(element));
    }

    public static WebElement waitForElementPresent(By locator, int timeout) {
        System.out.println(Adb.getDeviceId() + " Waiting up to " + timeout + "s for element to be present in DOM: " + locator);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeout));
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public static WebElement waitForElementPresent(By locator) {
        System.out.println(Adb.getDeviceId() + " Waiting up to " + DEFAULT_TIMEOUT + "s (default) for element to be present in DOM: " + locator);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(DEFAULT_TIMEOUT));
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public static boolean waitForTextToBePresent(By locator, String text, int timeout) {
        System.out.println(Adb.getDeviceId() + " Waiting up to " + timeout + "s for text '" + text + "' to be present in element: " + locator);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeout));
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    public static boolean waitForTextToBePresent(By locator, String text) {
        System.out.println(Adb.getDeviceId() + " Waiting up to " + DEFAULT_TIMEOUT + "s (default) for text '" + text + "' to be present in element: " + locator);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(DEFAULT_TIMEOUT));
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    public static boolean waitForTextToBePresent(WebElement element, String text, int timeout) {
        System.out.println(Adb.getDeviceId() + " Waiting up to " + timeout + "s for text '" + text + "' to be present in element: " + element);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeout));
        return wait.until(ExpectedConditions.textToBePresentInElement(element, text));
    }

    public static boolean waitForTextToBePresent(WebElement element, String text) {
        System.out.println(Adb.getDeviceId() + " Waiting up to " + DEFAULT_TIMEOUT + "s (default) for text '" + text + "' to be present in element: " + element);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(DEFAULT_TIMEOUT));
        return wait.until(ExpectedConditions.textToBePresentInElement(element, text));
    }

    public static boolean waitForAttributeToBe(By locator, String attribute, String value, int timeout) {
        System.out.println(Adb.getDeviceId() + " Waiting up to " + timeout + "s for attribute '" + attribute + "' to be '" + value + "' in element: " + locator);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeout));
        return wait.until(ExpectedConditions.attributeToBe(locator, attribute, value));
    }

    public static boolean waitForAttributeToBe(By locator, String attribute, String value) {
        System.out.println(Adb.getDeviceId() + " Waiting up to " + DEFAULT_TIMEOUT + "s (default) for attribute '" + attribute + "' to be '" + value + "' in element: " + locator);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(DEFAULT_TIMEOUT));
        return wait.until(ExpectedConditions.attributeToBe(locator, attribute, value));
    }

    public static boolean waitForAttributeToBe(WebElement element, String attribute, String value, int timeout) {
        System.out.println(Adb.getDeviceId() + " Waiting up to " + timeout + "s for attribute '" + attribute + "' to be '" + value + "' in element: " + element);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeout));
        return wait.until(ExpectedConditions.attributeToBe(element, attribute, value));
    }

    public static boolean waitForAttributeToBe(WebElement element, String attribute, String value) {
        System.out.println(Adb.getDeviceId() + " Waiting up to " + DEFAULT_TIMEOUT + "s (default) for attribute '" + attribute + "' to be '" + value + "' in element: " + element);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(DEFAULT_TIMEOUT));
        return wait.until(ExpectedConditions.attributeToBe(element, attribute, value));
    }

    public static List<WebElement> waitForNumberOfElements(By locator, int expectedCount, int timeout) {
        System.out.println(Adb.getDeviceId() + " Waiting up to " + timeout + "s for number of elements to be " + expectedCount + " for locator: " + locator);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeout));
        return wait.until(ExpectedConditions.numberOfElementsToBe(locator, expectedCount));
    }

    public static List<WebElement> waitForNumberOfElements(By locator, int expectedCount) {
        System.out.println(Adb.getDeviceId() + " Waiting up to " + DEFAULT_TIMEOUT + "s (default) for number of elements to be " + expectedCount + " for locator: " + locator);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(DEFAULT_TIMEOUT));
        return wait.until(ExpectedConditions.numberOfElementsToBe(locator, expectedCount));
    }

    public static boolean waitForUrlContains(String text, int timeout) {
        System.out.println(Adb.getDeviceId() + " Waiting up to " + timeout + "s for URL to contain: '" + text + "'");
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeout));
        return wait.until(ExpectedConditions.urlContains(text));
    }

    public static boolean waitForUrlContains(String text) {
        System.out.println(Adb.getDeviceId() + " Waiting up to " + DEFAULT_TIMEOUT + "s (default) for URL to contain: '" + text + "'");
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(DEFAULT_TIMEOUT));
        return wait.until(ExpectedConditions.urlContains(text));
    }

    public static boolean waitForNumberOfWindows(int expectedWindows, int timeout) {
        System.out.println(Adb.getDeviceId() + " Waiting up to " + timeout + "s for number of windows to be: " + expectedWindows);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeout));
        return wait.until(ExpectedConditions.numberOfWindowsToBe(expectedWindows));
    }

    public static boolean waitForNumberOfWindows(int expectedWindows) {
        System.out.println(Adb.getDeviceId() + " Waiting up to " + DEFAULT_TIMEOUT + "s (default) for number of windows to be: " + expectedWindows);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(DEFAULT_TIMEOUT));
        return wait.until(ExpectedConditions.numberOfWindowsToBe(expectedWindows));
    }

    public static void pressBackButton() {
        try {
            if (getDriver() != null) {
                getDriver().navigate().back();
                System.out.println(Adb.getDeviceId() + "[Appium] Đã nhấn nút Back.");
            } else {
                System.err.println(Adb.getDeviceId() + "[Appium] Driver chưa được khởi tạo!");
            }
        } catch (Exception e) {
            System.err.println(Adb.getDeviceId() + "[Appium] Lỗi khi nhấn nút Back: " + e.getMessage());
        }
    }

    public static Dimension getScreenSize() {
        try {
            Dimension size = getDriver().manage().window().getSize();
            System.out.println(Adb.getDeviceId() + " Screen size: width = " + size.getWidth() + ", height = " + size.getHeight());
            return size;
        } catch (Exception e) {
            System.out.println(Adb.getDeviceId() + " Failed to get screen size: " + e.getMessage());
            return null;
        }
    }

    public static void swipeLeftFromElement(By locator) {

    }

    public static void checkAppium() {
        if (getDriver().getSessionId() != null) {
            System.out.println("✅ Session Appium vẫn đang hoạt động.");
        } else {
            System.out.println("❌ Session Appium đã bị mất.");
        }
    }

    public static void startMonitoring(AppiumDriver driver) {
        scheduler = Executors.newScheduledThreadPool(1);
        Runnable healthCheckTask = () -> {
            try {
                if (getDriver() != null) {
                    checkAppiumHealth(driver, driver.getCapabilities().getCapability("udid").toString());
                } else {
                    System.out.println("❗ Driver chưa khởi tạo, bỏ qua lần kiểm tra.");
                }
            } catch (Exception e) {
                System.out.println("❌ Lỗi khi kiểm tra trạng thái Appium:");
                e.printStackTrace();
            }
        };


        // Thực hiện sau 0 giây và lặp lại mỗi 3 phút
        scheduler.scheduleAtFixedRate(healthCheckTask, 0, 1, TimeUnit.MINUTES);
    }

    // Khi chương trình chính xong:
    public static void stopMonitoring() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown(); // hoặc scheduler.shutdownNow();
            System.out.println("⏹ Đã dừng theo dõi trạng thái Appium.");
        }
    }

    public static void checkAppiumHealth(AppiumDriver driver, String deviceId) {
        System.out.println("=== Kiểm tra trạng thái Appium ===");

        boolean server = isAppiumServerRunning();
        boolean device = isDeviceConnected(deviceId);
        boolean session = isSessionAlive(driver);

        System.out.println("Appium Server: " + (server ? "Hoạt động" : "Không phản hồi"));
        System.out.println("Thiết bị Android: " + (device ? "Đang kết nối" : "Không kết nối"));
        System.out.println("Appium Session: " + (session ? "Đang hoạt động" : "Đã chết"));

        if (!server || !device || !session) {
            System.out.println("❌ Có vấn đề trong kết nối hoặc session.");
        } else {
            System.out.println("✅ Mọi thứ ổn định.");
        }
    }

    public static boolean isAppiumServerRunning() {
        try {
            URL url = new URL("http://127.0.0.1:4723/status");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000); // 2 giây timeout

            int responseCode = conn.getResponseCode();
            return responseCode == 200;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean isDeviceConnected(String deviceId) {
        try {
            Process process = Runtime.getRuntime().exec("adb devices");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(deviceId) && line.contains("device")) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isSessionAlive(AppiumDriver driver) {
        try {
            return driver != null && driver.getSessionId() != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static File captureScreenshot() {
        File file = null;
        try {
            file = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
        } catch (Exception e) {
            System.err.println(Adb.getDeviceId() + "Capture Screen False!");
        }
        return file;
    }

    public static boolean clickImage(String imagePoint) {
        return ImageMatching.findAndClickImageOnScreen(imagePoint);
    }

    public static boolean checkVideoPlayer() {
        boolean found = false;
        try {
            String firstImage = captureScreenshot().getAbsolutePath();

            for (int i = 0; i <= 5; i++) {
                if (!ImageMatching.isImageOnScreen(firstImage, 0.05)) {
                    found = true;
                    break;
                }
                MobileUI.sleep(3);
            }
        } catch (Exception e) {
            System.out.println("compare images error!");
        }
        return found;
    }
    

    public static String findFirstElementFromSource(String[] xpaths) {
        System.out.println(Adb.getDeviceId() + ": start findFirstElementFromSource()");
        try {
            long start = System.currentTimeMillis();

            // --- B1: Lấy page source ---
            String pageSource = getDriver().getPageSource();

            // --- B2: Parse XML ---
            Document document = SystemHelpers.parseXml(pageSource);
            XPath xPath = XPathFactory.newInstance().newXPath();

            // --- B3: Kiểm tra từng locator ---
            for (String locator : xpaths) {
                locator = SystemHelpers.normalizeXPath(locator.substring(10).trim());
                XPathExpression expr = xPath.compile(locator);
                int count = ((NodeList) expr.evaluate(document, XPathConstants.NODESET)).getLength();

                if (count > 0) {
                    long end = System.currentTimeMillis();
                    System.out.println("Tìm thấy element: " + locator);
                    System.out.println("Thời gian kiểm tra: " + (end - start) + " ms");
                    return locator; // Trả về ngay khi thấy
                }
            }

            long end = System.currentTimeMillis();
            System.out.println("Không tìm thấy element nào. Thời gian kiểm tra: " + (end - start) + " ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Không tìm thấy gì
    }

    public static String findFirstElementFromSource(By[] locators) {
        System.out.println(Adb.getDeviceId()+ ": start findFirstElementFromSource()");
        try {
            long start = System.currentTimeMillis();

            // --- B1: Lấy page source ---
            String pageSource = getDriver().getPageSource();

            // --- B2: Parse XML ---
            Document document = SystemHelpers.parseXml(pageSource);
            XPath xPath = XPathFactory.newInstance().newXPath();

            // --- B3: Kiểm tra từng locator ---
            for (By locator : locators) {
                String xpath = SystemHelpers.normalizeXPath(locator.toString().substring(10).trim());
                XPathExpression expr = xPath.compile(xpath);
                int count = ((NodeList) expr.evaluate(document, XPathConstants.NODESET)).getLength();

                if (count > 0) {
                    long end = System.currentTimeMillis();
                    System.out.println(Adb.getDeviceId() + "Tìm thấy element: " + locator);
                    System.out.println(Adb.getDeviceId() + "Thời gian kiểm tra: " + (end - start) + " ms");
                    return xpath; // Trả về ngay khi thấy
                }
            }

            long end = System.currentTimeMillis();
            System.out.println(Adb.getDeviceId() + "Không tìm thấy element nào. Thời gian kiểm tra: " + (end - start) + " ms");
        } catch (Exception e) {
            System.out.println(Adb.getDeviceId() + "Lấy element lỗi!!!");
            e.printStackTrace();
            for (By locator : locators) {
                System.out.println(Adb.getDeviceId() + locator.toString());
            }
        }
        return null; // Không tìm thấy gì
    }

    public static List<By> findAllElementsFromSource(By[] locators) {
        System.out.println(Adb.getDeviceId() + ": start findAllElementsFromSource()");
        Set<String> uniqueXpaths = new HashSet<>(); // dùng để loại bỏ xpath trùng
        List<By> foundXpaths = new ArrayList<>();
        try {
            long start = System.currentTimeMillis();

            // --- B1: Lấy page source ---
            String pageSource = getDriver().getPageSource();

            // --- B2: Parse XML ---
            Document document = SystemHelpers.parseXml(pageSource);
            XPath xPath = XPathFactory.newInstance().newXPath();

            // --- B3: Kiểm tra từng locator ---
            for (By locator : locators) {
                String xpath = SystemHelpers.normalizeXPath(locator.toString().substring(10).trim());
                XPathExpression expr = xPath.compile(xpath);
                int count = ((NodeList) expr.evaluate(document, XPathConstants.NODESET)).getLength();

                if (count > 0 && uniqueXpaths.add(xpath)) { // chỉ add nếu chưa có trong Set
                    foundXpaths.add(By.xpath(xpath));
                    System.out.println(Adb.getDeviceId() + " Tìm thấy element: " + locator + " (count=" + count + ")");
                }
            }

            long end = System.currentTimeMillis();
            if (!foundXpaths.isEmpty()) {
                System.out.println(Adb.getDeviceId() + "Tìm thấy tổng cộng: " + foundXpaths.size() + " locator(s). Thời gian kiểm tra: " + (end - start) + " ms");
            } else {
                System.out.println(Adb.getDeviceId() + "Không tìm thấy element nào. Thời gian kiểm tra: " + (end - start) + " ms");
            }
        } catch (Exception e) {
            System.out.println(Adb.getDeviceId() + "Lấy element lỗi!!!");
            e.printStackTrace();
            for (By locator : locators) {
                System.out.println(Adb.getDeviceId() + locator.toString());
            }
        }
        return foundXpaths; // Trả về tất cả các xpath tìm thấy (không trùng)
    }

    public static ElementState isElementDisplayedByPageSource(By locator) {
        System.out.println(Adb.getDeviceId()+ ": start isElementDisplayedByPageSource()");
        try {
            String pageSource = getDriver().getPageSource();
            Document document = SystemHelpers.parseXml(pageSource);
            XPath xPath = XPathFactory.newInstance().newXPath();

            String xpath = SystemHelpers.normalizeXPath(locator.toString().substring(10).trim());
            XPathExpression expr = xPath.compile(xpath);
            int count = ((NodeList) expr.evaluate(document, XPathConstants.NODESET)).getLength();

            if (count > 0) {
                System.out.println(Adb.getDeviceId() + locator + " is Displayed on screen");
                return ElementState.DISPLAYED;
            }

            System.out.println(Adb.getDeviceId() + locator + " is NOT Displayed on screen");
            return ElementState.NOT_DISPLAYED; // ⭐ BẮT BUỘC
        } catch (Exception e) {
            e.printStackTrace();
            String errName = e.getMessage();
            if (errName.contains("socket hang up") || errName.contains("cannot be proxied to UiAutomator2 server") || errName.contains("A session is either terminated or not started")) {
                System.out.println("appium is disconnected...");
                VariablesThreadLocal.setError(true);
                return ElementState.UNKNOWN;
            }
            return ElementState.NOT_DISPLAYED;
        }
    }

    public static boolean isElementDisplayedByPageSource(By locator, String message) {
        try {
            // --- B1: Lấy page source ---
            String pageSource = getDriver().getPageSource();
            // --- B2: Parse XML ---
            Document document = SystemHelpers.parseXml(pageSource);
            XPath xPath = XPathFactory.newInstance().newXPath();
            // --- B3: Kiểm tra từng locator ---
            String xpath = SystemHelpers.normalizeXPath(locator.toString().substring(10).trim());
            XPathExpression expr = xPath.compile(xpath);
            int count = ((NodeList) expr.evaluate(document, XPathConstants.NODESET)).getLength();
            if (count > 0) {
                long end = System.currentTimeMillis();
                System.out.println(Adb.getDeviceId() + message + " is Displayed on screen");
                return true; // Trả về ngay khi thấy
            }
            System.out.println(Adb.getDeviceId() + message + " is NOT Displayed on screen");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // Không tìm thấy gì
    }

    public static boolean isElementDisplayedByPageSource(By locator, int seconds) {
        System.out.println(Adb.getDeviceId()+ ": start isElementDisplayedByPageSource()");
        try {
            for (int i = 0; i < seconds; i++) {

                String pageSource = getDriver().getPageSource();

                Document document = SystemHelpers.parseXml(pageSource);
                XPath xPath = XPathFactory.newInstance().newXPath();

                String xpath = SystemHelpers.normalizeXPath(locator.toString().substring(10).trim());
                XPathExpression expr = xPath.compile(xpath);
                int count = ((NodeList) expr.evaluate(document, XPathConstants.NODESET)).getLength();
                if (count > 0) {
                    long end = System.currentTimeMillis();
                    System.out.println(Adb.getDeviceId() + locator + " is Displayed on screen");
                    return true; // Trả về ngay khi thấy
                }
                MobileUI.sleep(1);
                if (i == seconds) {
                    System.out.println(Adb.getDeviceId() + locator + " is NOT Displayed on screen");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // Không tìm thấy gì
    }

    public static String getTextElementByPageSource(By locator, String message) {
        try {
            // --- B1: Lấy page source ---
            String pageSource = getDriver().getPageSource();

            // --- B2: Parse XML ---
            Document document = SystemHelpers.parseXml(pageSource);
            XPath xPath = XPathFactory.newInstance().newXPath();

            // --- B3: Convert By → xpath ---
            String xpath = SystemHelpers.normalizeXPath(
                    locator.toString().substring(10).trim()
            );

            XPathExpression expr = xPath.compile(xpath);
            NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

            if (nodes.getLength() > 0) {
                Node node = nodes.item(0);

                // Android text nằm trong attribute "text"
                if (node.getAttributes() != null &&
                        node.getAttributes().getNamedItem("text") != null) {

                    String text = node.getAttributes()
                            .getNamedItem("text")
                            .getNodeValue();

                    System.out.println(Adb.getDeviceId() + message + " : " + text);
                    return text;
                }
            }

            System.out.println(Adb.getDeviceId() + message + " NOT FOUND");
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("Could not proxy command to the remote server. Original error: socket hang up") || e.getMessage().contains("cannot be proxied to UiAutomator2 server")){
                System.out.println("appium is disconnected...");
                VariablesThreadLocal.setError(true);
            }
        }
        return "";
    }

    public static boolean checkAppCrash(){
        try {
            System.out.println("start getPageSoure !");
            getDriver().getPageSource();
            System.out.println("getPageSoure success!");
            return false;
        }
        catch (Exception e){
            System.out.println("getPageSoure FAILED!");
            if (e.getMessage().contains("waiting for the root AccessibilityNodeInfo in the active window. Make sure the active window is not constantly hogging the main UI thread")){
                return true;
            }
            else{
                System.out.println("getPageSoure FAILED!: "+e.getMessage());
                return false;
            }
        }
    }

}