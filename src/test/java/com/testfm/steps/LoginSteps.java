package com.testfm.steps;

import com.testfm.driver.DriverManager;
import com.testfm.pages.LoginPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

/**
 * Step Definitions for New York Cares Login feature.
 *
 * Tag: @Login
 * Run only this test via:
 *   mvn test -Dcucumber.filter.tags="@Login"
 */
public class LoginSteps {

    private final LoginPage loginPage;

    public LoginSteps() {
        this.loginPage = new LoginPage(DriverManager.getDriver());
    }

    @Given("I navigate to the New York Cares login page")
    public void iNavigateToTheNewYorkCaresLoginPage() {
        loginPage.open();
        System.out.println("[Step] Navigated to New York Cares login page.");
    }

    @When("I enter the username {string}")
    public void iEnterTheUsername(String username) {
        loginPage.enterUsername(username);
        System.out.println("[Step] Username entered: " + username);
    }

    @And("I enter the password {string}")
    public void iEnterThePassword(String password) {
        loginPage.enterPassword(password);
        System.out.println("[Step] Password entered.");
    }

    @And("I click the Login button")
    public void iClickTheLoginButton() {
        loginPage.clickLoginButton();
        System.out.println("[Step] Login button clicked.");
    }

     @Then("I should be logged in successfully and redirected to the dashboard page")
    public void iShouldBeLoggedInAndRedirectedToTheDashboardPage() {
        boolean onDashboard = loginPage.isOnDashboard();
        System.out.println("[Step] On dashboard: " + onDashboard);
        Assertions.assertTrue(
            onDashboard,
            "Login FAILED — Expected to land on /dashboard but current URL is: "
                + loginPage.getCurrentUrl()
        );

        loginPage.waitAfterSuccessfulLogin();
        System.out.println("[Step] Waited 15s after successful login.");
    }
}
