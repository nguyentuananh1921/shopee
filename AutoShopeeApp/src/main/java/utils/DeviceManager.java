package utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import utils.helpers.SystemHelpers;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class DeviceManager {

    private AppiumDriverLocalService service;
    private String HOST = "127.0.0.1";
    private String PORT;
    private String deviceID;
    private int TIMEOUT_SERVICE = 3000;

    public DeviceManager(String PORT, String deviceID){
        this.PORT = PORT;
        this.deviceID = deviceID;
    }
    public void runAppiumServer() {
        //Kill process on port
        SystemHelpers.killPort(getPORT());
//        SystemHelpers.connectDevice(getDeviceID());
        //Build the Appium service
        AppiumServiceBuilder builder = new AppiumServiceBuilder();

        builder.withIPAddress(HOST);
        builder.usingPort(Integer.parseInt(getPORT()));
//        builder.withArgument(GeneralServerFlag.LOG_LEVEL, "info"); // Set log level (optional)
        builder.withArgument(GeneralServerFlag.LOG_LEVEL,"error");
        builder.withTimeout(Duration.ofSeconds(TIMEOUT_SERVICE));

        //Start the server with the builder
        service = AppiumDriverLocalService.buildService(builder);
        service.start();

        if (service.isRunning()) {
            System.out.println("##### Appium server started on " + HOST + ":" + getPORT());
        } else {
            System.out.println("Failed to start Appium server.");
        }

    }

    public void setUpDriver() {
        AppiumDriver driver;
        UiAutomator2Options options = new UiAutomator2Options();

        System.out.println("***SERVER ADDRESS: " + HOST);
        System.out.println("***SERVER POST: " + getPORT());

        options.setPlatformName("Android");
        options.setPlatformVersion("7.1.2");
        options.setAutomationName("UiAutomator2");
        options.setUdid(getDeviceID());
        options.setDeviceName("device");
        options.setAppPackage("com.shopee.vn");
        options.setAppActivity("com.shopee.app.ui.home.HomeActivity_");
        options.setAdbExecTimeout(Duration.ofSeconds(60));
//        options.setNoReset(false);
//        options.setFullReset(false);
//        options.setAutoGrantPermissions(true);
//        options.setCapability("ignoreHiddenApiPolicyError", true);
//        options.setCapability("appium:skipServerInstallation", false);
        boolean connected = false;
        for (int i = 0; i<=5; i++){
            try {
                driver = new AppiumDriver(new URL("http://" + HOST + ":" + getPORT()), options);
                DriverManager.setDriver(driver);
                System.out.println(driver.getRemoteAddress().toString());
                connected = true;
                if (connected) break;
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        DriverManager.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
    }

    public void setUpDriverTest() {
        AppiumDriver driver;
        UiAutomator2Options options = new UiAutomator2Options();

        System.out.println("***SERVER ADDRESS: " + HOST);
        System.out.println("***SERVER POST: " + getPORT());

        options.setPlatformName("Android");
//        options.setPlatformVersion("9");
        options.setAutomationName("UiAutomator2");
        options.setUdid(getDeviceID());
        options.setDeviceName("device");

        options.setAdbExecTimeout(Duration.ofSeconds(60));
        options.setUiautomator2ServerInstallTimeout(Duration.ofSeconds(60));
        options.setUiautomator2ServerLaunchTimeout(Duration.ofSeconds(60));
        options.setNoReset(true);
        options.setFullReset(false);
        options.setAutoGrantPermissions(true);
        options.setAppPackage("com.shopee.vn");
        options.setAppActivity("com.shopee.app.ui.home.HomeActivity_");
        options.setAdbExecTimeout(Duration.ofSeconds(60));
        options.setCapability("ignoreHiddenApiPolicyError", true);

        try {
            driver = new AppiumDriver(new URL("http://" + HOST + ":" + getPORT()), options);
            DriverManager.setDriver(driver);
            System.out.println(driver.getRemoteAddress().toString());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        DriverManager.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
    }

    public void tearDownDriver() {
        if (DriverManager.getDriver() != null) {
            DriverManager.quitDriver();
            System.out.println("##### Driver quit and removed.");
        }
    }

    public void stopAppiumServer() {
        if (service != null && service.isRunning()) {
            service.stop();
            System.out.println("##### Appium server stopped.");
        }
    }

    public String getPORT() {
        return PORT;
    }

    public void setPORT(String PORT) {
        this.PORT = PORT;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }


}