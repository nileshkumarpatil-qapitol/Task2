package org.orangehrm.smoke;

import org.orangehrm.base.DriverManager;
//import org.orangehrm.base.ExtentManager;
import org.orangehrm.base.TestData;
import org.orangehrm.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SmokeTests extends LoginPage {

    @Test(
            priority = 1,
            enabled = true,
            description = "Add a New Skill Successfully"
    )
    public void TC_OHRM_AD_QF_001() throws InterruptedException {
        // Initialize the page object
        LoginPage loginPage = new LoginPage();
        // Open the application URL
        loginPage.openUrl();
        // Perform login
        loginPage.login("Admin", "admin123");
        // Validate login success by checking the page title
        String actualTitle = DriverManager.getDriver().getTitle();
        Assert.assertTrue(actualTitle.contains(TestData.get("title")), "Login failed!");

        // clicked on admin in side bar a
        loginPage.adminSideBar();
        loginPage.clickMenu("Qualifications ");
        loginPage.clickSubMenu("Skills");
        // click on add button
        loginPage.clickOnAddBtn();

        //Generating random string for skills name
        String randomSkillsName = generateRandomString(6);
        loginPage.addSkills(randomSkillsName, (randomSkillsName + "some description"));
        loginPage.clickSaveBtn();

        // verifying the newly added skills is displayed in table
        Assert.assertEquals(loginPage.getTabelData(randomSkillsName),randomSkillsName, "Added skills is not visible");
    }

    @Test(
            priority = 2,
            enabled = true,
            description = "Add Skill Without Name (Negative Test)"
    )
    public void TC_OHRM_AD_QF_002() throws InterruptedException {
        // Initialize the page object
        LoginPage loginPage = new LoginPage();
        // Open the application URL
        loginPage.openUrl();
        // clicked on admin in side bar a
        loginPage.adminSideBar();
        loginPage.clickMenu("Qualifications ");
        loginPage.clickSubMenu("Skills");
        // click on add button
        loginPage.clickOnAddBtn();

        //Generating random string for skills name
//        String randomSkillsName = generateRandomString(6);
        loginPage.addSkills("Java", "some description");

        // verifying the newly added skills is displayed in table
        Assert.assertEquals(loginPage.checkErrorMessage(), true, "Error message for Already exists is not visible");
    }
}
