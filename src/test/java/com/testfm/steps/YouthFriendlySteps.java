package com.testfm.steps;

import com.testfm.driver.DriverManager;
import com.testfm.pages.LoginPage;
import com.testfm.pages.YouthFriendlyPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class YouthFriendlySteps {

    private final LoginPage loginPage;
    private final YouthFriendlyPage youthFriendlyPage;

    public YouthFriendlySteps() {
        this.loginPage = new LoginPage(DriverManager.getDriver());
        this.youthFriendlyPage = new YouthFriendlyPage(DriverManager.getDriver());
    }

    @Given("I log in for youth friendly flow with username {string} and password {string}")
    public void iLogInForYouthFriendlyFlowWithUsernameAndPassword(String username, String password) {
        loginPage.open();
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickLoginButton();

        boolean onDashboard = loginPage.isOnDashboard();
        Assertions.assertTrue(
            onDashboard,
            "Youth friendly login failed. Current URL: " + loginPage.getCurrentUrl()
        );
        System.out.println("[Step] Logged in for youth friendly flow.");
    }

    @When("I click the Youth Friendly button")
    public void iClickTheYouthFriendlyButton() {
        youthFriendlyPage.clickYouthFriendlyButton();
        System.out.println("[Step] Clicked Youth Friendly button.");
    }

    @And("I click the Family Roaster button")
    public void iClickTheFamilyRoasterButton() {
        youthFriendlyPage.clickFamilyRoasterButton();
        System.out.println("[Step] Clicked Family Roaster button.");
    }

    @Then("I should see the youth friendly heading {string}")
    public void iShouldSeeTheYouthFriendlyHeading(String expectedHeading) {
        String actualHeading = youthFriendlyPage.getFamilyMemberHeadingText();
        Assertions.assertEquals(
            expectedHeading,
            actualHeading,
            "Expected youth friendly heading did not match."
        );
        System.out.println("[Step] Verified heading: " + actualHeading);
    }
}
