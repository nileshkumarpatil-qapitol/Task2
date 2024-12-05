package org.orangehrm.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.orangehrm.base.DriverManager;

import java.time.Duration;

public class ElementActions {

    private static final Logger logger = LogManager.getLogger(ElementActions.class);

    // waitAndClick Method using FluentWait
    public void waitAndClick(By locator) {
        FluentWait<WebDriver> wait = new FluentWait<>(DriverManager.getDriver())
                .withTimeout(Duration.ofSeconds(30))       // Default timeout
                .pollingEvery(Duration.ofMillis(500))      // Polling interval
                .ignoring(Exception.class);                // Ignore common exceptions

        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        element.click();
        logger.info("Clicked on element located by: " + locator);
    }

    // waitAndClick Method for WebElement
    public void waitAndClick(WebElement element) {
        FluentWait<WebDriver> wait = new FluentWait<>(DriverManager.getDriver())
                .withTimeout(Duration.ofSeconds(30))       // Default timeout
                .pollingEvery(Duration.ofMillis(500))      // Polling interval
                .ignoring(Exception.class);                // Ignore common exceptions

        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();
        logger.info("Clicked on element: " + element);
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
        logger.info("Set text using JavaScript: '" + text + "' in element: " + element);
    }
}
