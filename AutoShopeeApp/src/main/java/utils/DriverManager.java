package utils;

import io.appium.java_client.AppiumDriver;

public class DriverManager {
    private static final ThreadLocal<AppiumDriver> drivers = new ThreadLocal<>();

    public static void setDriver(AppiumDriver driverInstance) {
        drivers.set(driverInstance);
    }

    public static AppiumDriver getDriver() {
        return drivers.get();
    }

    public static void closeDriver() {
        if (drivers.get() != null) {
            getDriver().close();
        }
    }

    public static void quitDriver() {
        if (drivers.get() != null) {
            getDriver().quit();
            drivers.remove();
        }
    }
}
