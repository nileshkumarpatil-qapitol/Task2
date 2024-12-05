package org.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.orangehrm.base.DriverManager;

import java.util.Random;

public class LoginPage extends com.qapitol.base.BaseClass {

    // Constructor to initialize WebElements
    public LoginPage() {
        PageFactory.initElements(DriverManager.getDriver(), this); // Use DriverManager
    }

    // Locators for WebElements
    @FindBy(name = "username")
    private WebElement usernameField;

    @FindBy(name = "password")
    private WebElement passwordField;

    @FindBy(xpath = "//button[.=' Login ']")
    private WebElement loginButton;

    @FindBy(xpath = "//button[.=' Add ']")
    private WebElement addButton;

    @FindBy(xpath = "//label[.='Name']/../..//input")
    private WebElement skillsNameInput;

    @FindBy(xpath = "//button[.=' Save ']")
    private WebElement saveBtn;

    @FindBy(xpath = "//a[@class='oxd-main-menu-item']//span[.='Admin']")
    private WebElement adminSideBar;

    @FindBy(xpath = "//textarea[@placeholder='Type description here']")
    private WebElement skillsDescriptionInput;

    @FindBy(xpath = "//span[.='Already exists']")
    private WebElement alreadyExistsErrorMsg;

    // Open URL Method
    public void openUrl() {
        DriverManager.getDriver().get("https://opensource-demo.orangehrmlive.com/");
    }

    // Login Method
    public void login(String username, String password) throws InterruptedException {
        Thread.sleep(3000);
        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        loginButton.click();
    }

    public void clickMenu(String menuText) {
        By menuLocator = By.xpath("//span[text()='" + menuText + "']");
        waitAndClick(menuLocator);
    }

    public void clickSubMenu(String submenuText) throws InterruptedException {
        Thread.sleep(3000);
        By submenuLocator = By.xpath("//li//a[.='"+submenuText+"']");
        clickElementUsingJS(DriverManager.getDriver().findElement(submenuLocator));
    }

    public String getTabelData(String data) throws InterruptedException {
        Thread.sleep(5000);
        By actualText = By.xpath("(//div[@class='oxd-table-cell oxd-padding-cell']/div[.='"+data+"'])[1]");
        waitForVisibility(DriverManager.getDriver().findElement(actualText));
        return DriverManager.getDriver().findElement(actualText).getText();

    }

    public void clickOnAddBtn(){
        waitAndClick(addButton);
    }

    public void clickSaveBtn(){
        waitAndClick(saveBtn);
    }

    public void adminSideBar(){
        waitAndClick(adminSideBar);
    }

    public void addSkills(String name, String description) throws InterruptedException {
        Thread.sleep(3000);
        skillsNameInput.sendKeys(name);
        skillsDescriptionInput.sendKeys(name);
    }

    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            stringBuilder.append(characters.charAt(index));
        }

        return stringBuilder.toString();
    }

    public boolean checkErrorMessage(){
        return waitForVisibility(alreadyExistsErrorMsg).isDisplayed();
    }



}
