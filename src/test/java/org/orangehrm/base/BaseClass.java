package com.qapitol.base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.orangehrm.base.DriverManager;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Duration;

public class BaseClass {

    // WebDriver and ExtentReports related variables
    protected ExtentReports extent;
    protected ExtentTest test;

    // Log4j Logger instance
    private static final Logger logger = LogManager.getLogger(BaseClass.class);

    public void openlink(String url) {
        DriverManager.getDriver().get(url);
        DriverManager.getDriver().manage().window().maximize();
        logger.info("Opened URL: " + url);
    }

    // waitAndClick Method using FluentWait
    public void waitAndClick(By locator) {
        FluentWait<WebDriver> wait = new FluentWait<>(DriverManager.getDriver())
                .withTimeout(Duration.ofSeconds(30))       // Default timeout
                .pollingEvery(Duration.ofMillis(500))      // Polling interval
                .ignoring(Exception.class);                // Ignore common exceptions

        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        element.click();
    }

    public void clickUsingActions(WebElement element) {
        Actions actions = new Actions(DriverManager.getDriver());
        actions.moveToElement(element).click().perform();
        logger.info("Performed click action on element: " + element);
    }

    public void scrollToElement(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
        js.executeScript("arguments[0].scrollIntoView(true);", element);
        logger.debug("Scrolled to element: " + element);
    }

    public void setText(WebElement element, String text) {
        scrollToElement(element);
        element.clear();
        element.sendKeys(text);
        logger.info("Set text: '" + text + "' in element: " + element);
    }

    // Generic waitForElement Method
    public WebElement waitForElement(WebElement element, ExpectedCondition<WebElement> condition) {
        FluentWait<WebDriver> wait = new FluentWait<>(DriverManager.getDriver())
                .withTimeout(Duration.ofSeconds(30))       // Default timeout
                .pollingEvery(Duration.ofMillis(500))      // Polling interval
                .ignoring(Exception.class);                // Ignore common exceptions

        return wait.until(condition);
    }

    public WebElement waitForVisibility(WebElement element) {
        return waitForElement(element, ExpectedConditions.visibilityOf(element));
    }

    public WebElement waitForClickability(WebElement element) {
        return waitForElement(element, ExpectedConditions.elementToBeClickable(element));
    }

    public void clickElementUsingJS(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
        js.executeScript("arguments[0].click();", element);
        logger.info("Clicked element using JavaScript: " + element);
    }

    public static void setTextUsingJS(WebElement element, String text) {
        // Check if the driver is set
        if (DriverManager.getDriver() == null) {
            throw new IllegalStateException("WebDriver is not initialized. Call setDriver() first.");
        }

        // Create JavaScriptExecutor instance
        JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();

        // Execute JavaScript to set the value of the input field
        js.executeScript("arguments[0].value = arguments[1];", element, text);
    }

    // Close the browser (only called after all tests are completed)
    public void closeBrowser() {
        DriverManager.getDriver().quit();
        logger.info("Browser closed.");
    }

    @Parameters({"browser"})
    @BeforeClass
    public void setupReport(@Optional ("chrome")String browser) {
        DriverManager.setDriver(browser);

        // Ensure report folder exists
        String reportDir = System.getProperty("user.dir") + "/test-output/";
        File reportDirFile = new File(reportDir);
        if (!reportDirFile.exists()) {
            reportDirFile.mkdirs();
        }

        // Setup Extent Spark Reporter
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportDir + "ExtentReport.html");
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Browser", "Chrome");
        extent.setSystemInfo("Environment", "QA");
        logger.info("Extent report setup complete.");
    }

    @BeforeMethod
    public void createTest(Method method) {
        test = extent.createTest(method.getName());
        logger.info("Test started: " + method.getName());
    }

    @AfterMethod
    public void afterMethod(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            // Log the failure
            test.fail("Test failed: " + result.getThrowable());
            try {
                // Capture and add a screenshot to the report
                String screenshotPath = captureScreenshot(result.getName());
                test.addScreenCaptureFromPath(screenshotPath, "Screenshot on Failure");
            } catch (IOException e) {
                test.fail("Failed to attach screenshot due to: " + e.getMessage());
            }
            logger.error("Test failed: " + result.getThrowable());
        } else {
            test.pass("Test passed");
            logger.info("Test passed: " + result.getName());
        }
    }

    @AfterClass
    public void flushReport() {
        extent.flush();
        logger.info("Extent report flushed.");
    }

    // AfterSuite method to close the browser after all test suites are finished
    @AfterClass
    public void tearDownSuite() {
        if (DriverManager.getDriver() != null) {
            DriverManager.getDriver().quit();  // Close the browser after all tests are done
            logger.info("Browser closed after all tests are completed.");
        }
    }

    private String captureScreenshot(String screenshotName) throws IOException {
        // Get the absolute path for saving screenshots
        String screenshotDir = System.getProperty("user.dir") + "/test-output/screenshots/";
        File screenshotDirFile = new File(screenshotDir);
        if (!screenshotDirFile.exists()) {
            screenshotDirFile.mkdirs();  // Create the directory if it doesn't exist
        }

        // Take screenshot
        TakesScreenshot ts = (TakesScreenshot) DriverManager.getDriver();
        File source = ts.getScreenshotAs(OutputType.FILE);

        // Specify the destination path for the screenshot
        String destination = screenshotDir + screenshotName + ".png";
        File finalDestination = new File(destination);

        // Copy the screenshot to the desired destination
        FileUtils.copyFile(source, finalDestination);

        // Return relative path for the report
        // Use a relative path based on the location of the report
        return "screenshots/" + screenshotName + ".png";
    }

    // Simplified method to log a passed assertion
    protected void logPass(String message) {
        test.pass(message);
        logger.info(message);
        Assert.assertTrue(true, message); // This ensures the test continues as passed
    }

    // Method to log a failed assertion
    protected void logFail(String message) {
        test.fail(message);
        logger.error(message);
        Assert.fail(message); // To fail the test explicitly
    }

    // waitAndClick Method for WebElement
    public void waitAndClick(WebElement element) {
        FluentWait<WebDriver> wait = new FluentWait<>(DriverManager.getDriver())
                .withTimeout(Duration.ofSeconds(30))       // Default timeout
                .pollingEvery(Duration.ofMillis(500))      // Polling interval
                .ignoring(Exception.class);                // Ignore common exceptions

        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();
    }

    protected void loggerMsg(String message) {
        logger.info(message);
    }
}