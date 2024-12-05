package org.orangehrm.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.*;
import java.util.Properties;

public class FrameworkUtils {
    private static final Logger log = LogManager.getLogger(FrameworkUtils.class);
    private static final String CONFIG_FILE = "src/main/resources/config.properties";

    // Properties Reader
    public static String readProperty(String key) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
            return properties.getProperty(key);
        } catch (IOException e) {
            log.error("Failed to read property", e);
            return null;
        }
    }

    // Properties Writer
    public static void writeProperty(String key, String value) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(CONFIG_FILE);
             OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.load(input);
            properties.setProperty(key, value);
            properties.store(output, null);
        } catch (IOException e) {
            log.error("Failed to write property", e);
        }
    }

    // JS Click
    public static void jsClick(WebDriver driver, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", element);
    }
}
